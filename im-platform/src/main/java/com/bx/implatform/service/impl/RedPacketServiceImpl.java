package com.bx.implatform.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.imclient.IMClient;
import com.bx.imcommon.model.IMGroupMessage;
import com.bx.imcommon.model.IMPrivateMessage;
import com.bx.imcommon.model.IMUserInfo;
import com.bx.implatform.dto.ReceiveRedPacketDTO;
import com.bx.implatform.dto.SendRedPacketDTO;
import com.bx.implatform.entity.GroupMember;
import com.bx.implatform.entity.GroupMessage;
import com.bx.implatform.entity.PrivateMessage;
import com.bx.implatform.entity.RedPacket;
import com.bx.implatform.entity.RedPacketReceive;
import com.bx.implatform.entity.User;
import com.bx.implatform.entity.UserWallet;
import com.bx.implatform.enums.MessageStatus;
import com.bx.implatform.enums.MessageType;
import com.bx.implatform.enums.RedPacketStatusEnum;
import com.bx.implatform.enums.RedPacketTypeEnum;
import com.bx.implatform.enums.TransactionStatusEnum;
import com.bx.implatform.enums.TransactionTypeEnum;
import com.bx.implatform.exception.GlobalException;
import com.bx.implatform.mapper.GroupMessageMapper;
import com.bx.implatform.mapper.PrivateMessageMapper;
import com.bx.implatform.mapper.RedPacketMapper;
import com.bx.implatform.mapper.RedPacketReceiveMapper;
import com.bx.implatform.mapper.UserWalletMapper;
import com.bx.implatform.service.GroupMemberService;
import com.bx.implatform.service.RedPacketService;
import com.bx.implatform.service.UserService;
import com.bx.implatform.service.UserWalletService;
import com.bx.implatform.service.WalletTransactionService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.session.UserSession;
import com.bx.implatform.util.BeanUtils;
import com.bx.implatform.vo.GroupMessageVO;
import com.bx.implatform.vo.PrivateMessageVO;
import com.bx.implatform.vo.RedPacketReceiveVO;
import com.bx.implatform.vo.RedPacketVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 红包服务实现
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedPacketServiceImpl extends ServiceImpl<RedPacketMapper, RedPacket> implements RedPacketService {

    private final UserWalletService userWalletService;
    private final UserWalletMapper userWalletMapper;
    private final WalletTransactionService walletTransactionService;
    private final RedPacketMapper redPacketMapper;
    private final RedPacketReceiveMapper redPacketReceiveMapper;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PrivateMessageMapper privateMessageMapper;
    private final GroupMessageMapper groupMessageMapper;
    private final GroupMemberService groupMemberService;
    private final IMClient imClient;

    private static final String RED_PACKET_LOCK_KEY = "red_packet:lock:";
    private static final int RED_PACKET_EXPIRE_HOURS = 24;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RedPacketVO sendRedPacket(SendRedPacketDTO dto) {
        Long senderId = SessionContext.getSession().getUserId();

        // 验证红包金额
        if (dto.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalException("红包金额必须大于0");
        }

        // 验证红包个数
        if (dto.getTotalCount() <= 0) {
            throw new GlobalException("红包个数必须大于0");
        }

        // 验证红包参数
        validateRedPacket(dto);

        // 验证支付密码
        if (!userWalletService.verifyPayPassword(senderId, dto.getPayPassword())) {
            throw new GlobalException("支付密码错误");
        }

        // 查询发送方钱包
        UserWallet senderWallet = userWalletService.getByUserId(senderId);

        // 检查余额是否充足
        if (senderWallet.getBalance().compareTo(dto.getTotalAmount()) < 0) {
            throw new GlobalException("账户余额不足");
        }

        // 生成红包编号
        String packetNo = "RP" + IdUtil.getSnowflakeNextIdStr();

        // 记录交易前余额
        BigDecimal beforeBalance = senderWallet.getBalance();

        // 创建交易流水
        String transactionNo = walletTransactionService.createTransaction(
                senderId, 0L, dto.getTotalAmount(),
                TransactionTypeEnum.RED_PACKET.getCode(),
                packetNo, "发红包"
        );

        // 冻结余额（使用乐观锁）
        int freezeResult = userWalletMapper.freezeBalance(
                senderId, dto.getTotalAmount(), senderWallet.getVersion()
        );

        if (freezeResult == 0) {
            log.error("冻结余额失败，可能是并发冲突, userId:{}, amount:{}", senderId, dto.getTotalAmount());
            throw new GlobalException("发红包失败，请重试");
        }

        // 创建红包记录
        RedPacket redPacket = new RedPacket();
        redPacket.setPacketNo(packetNo);
        redPacket.setSenderId(senderId);
        redPacket.setPacketType(dto.getPacketType());
        redPacket.setTotalAmount(dto.getTotalAmount());
        redPacket.setTotalCount(dto.getTotalCount());
        redPacket.setRemainingAmount(dto.getTotalAmount());
        redPacket.setRemainingCount(dto.getTotalCount());
        redPacket.setStatus(RedPacketStatusEnum.SENDING.getCode());
        redPacket.setChatType(dto.getChatType());
        redPacket.setTargetId(dto.getTargetId());
        redPacket.setMessage(dto.getMessage());

        // 设置过期时间（24小时后）
        Date expireTime = Date.from(LocalDateTime.now()
                .plusHours(RED_PACKET_EXPIRE_HOURS)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        redPacket.setExpireTime(expireTime);

        this.save(redPacket);

        // 计算交易后余额
        BigDecimal afterBalance = beforeBalance.subtract(dto.getTotalAmount());

        // 更新交易状态为成功
        walletTransactionService.updateTransactionStatus(
                transactionNo,
                TransactionStatusEnum.SUCCESS.getCode(),
                beforeBalance,
                afterBalance
        );

        log.info("发红包成功, packetNo:{}, senderId:{}, amount:{}, count:{}",
                packetNo, senderId, dto.getTotalAmount(), dto.getTotalCount());

        // 构造返回结果
        RedPacketVO vo = buildRedPacketVO(redPacket, senderId);
        
        // 创建消息记录并推送
        UserSession session = SessionContext.getSession();
        createAndSendRedPacketMessage(dto, packetNo, session, vo);
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal receiveRedPacket(ReceiveRedPacketDTO dto) {
        Long receiverId = SessionContext.getSession().getUserId();
        String packetNo = dto.getPacketNo();

        // 使用Redis分布式锁防止重复领取
        String lockKey = RED_PACKET_LOCK_KEY + packetNo + ":" + receiverId;
        Boolean lockResult = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(lockResult)) {
            throw new GlobalException("操作太频繁，请稍后再试");
        }

        try {
            // 查询红包信息
            LambdaQueryWrapper<RedPacket> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RedPacket::getPacketNo, packetNo);
            RedPacket redPacket = this.getOne(wrapper);

            if (redPacket == null) {
                throw new GlobalException("红包不存在");
            }

            // 验证红包状态
            if (!RedPacketStatusEnum.SENDING.getCode().equals(redPacket.getStatus())) {
                if (RedPacketStatusEnum.FINISHED.getCode().equals(redPacket.getStatus())) {
                    throw new GlobalException("红包已被抢完");
                } else if (RedPacketStatusEnum.EXPIRED.getCode().equals(redPacket.getStatus())) {
                    throw new GlobalException("红包已过期");
                } else {
                    throw new GlobalException("红包状态异常");
                }
            }

            // 验证是否已过期
            if (redPacket.getExpireTime().before(new Date())) {
                redPacket.setStatus(RedPacketStatusEnum.EXPIRED.getCode());
                this.updateById(redPacket);
                throw new GlobalException("红包已过期");
            }

            // 验证是否已领取
            LambdaQueryWrapper<RedPacketReceive> receiveWrapper = new LambdaQueryWrapper<>();
            receiveWrapper.eq(RedPacketReceive::getPacketId, redPacket.getId())
                         .eq(RedPacketReceive::getReceiverId, receiverId);
            RedPacketReceive existReceive = redPacketReceiveMapper.selectOne(receiveWrapper);

            if (existReceive != null) {
                throw new GlobalException("您已领取过该红包");
            }

            // 验证剩余个数
            if (redPacket.getRemainingCount() <= 0) {
                redPacket.setStatus(RedPacketStatusEnum.FINISHED.getCode());
                this.updateById(redPacket);
                throw new GlobalException("红包已被抢完");
            }

            // 计算领取金额
            BigDecimal receiveAmount = calculateReceiveAmount(redPacket);

            // 扣减红包剩余金额和个数（使用乐观锁）
            int deductResult = redPacketMapper.deductRedPacket(redPacket.getId(), receiveAmount);

            if (deductResult == 0) {
                log.error("扣减红包失败，可能是并发冲突或红包已被抢完, packetId:{}", redPacket.getId());
                throw new GlobalException("红包已被抢完");
            }

            // 创建领取记录
            RedPacketReceive receive = new RedPacketReceive();
            receive.setPacketId(redPacket.getId());
            receive.setReceiverId(receiverId);
            receive.setAmount(receiveAmount);
            receive.setIsBestLuck(0);
            receive.setReceiveTime(new Date());
            redPacketReceiveMapper.insert(receive);

            // 增加用户余额
            UserWallet receiverWallet = userWalletService.getOrCreateWallet(receiverId);
            
            // 扣减发送者冻结金额
            UserWallet senderWallet = userWalletService.getByUserId(redPacket.getSenderId());
            int deductFrozenResult = userWalletMapper.deductFrozenBalance(
                    redPacket.getSenderId(), receiveAmount, senderWallet.getVersion()
            );

            if (deductFrozenResult == 0) {
                log.error("扣减冻结金额失败, userId:{}, amount:{}", redPacket.getSenderId(), receiveAmount);
                throw new GlobalException("领取红包失败，请联系客服");
            }

            // 增加领取者余额（使用乐观锁，重试机制）
            int maxRetry = 3;
            boolean addSuccess = false;
            for (int i = 0; i < maxRetry; i++) {
                receiverWallet = userWalletService.getByUserId(receiverId);
                int addResult = userWalletMapper.addBalance(
                        receiverId, receiveAmount, receiverWallet.getVersion()
                );
                if (addResult > 0) {
                    addSuccess = true;
                    break;
                }
                log.warn("增加余额失败，重试中, userId:{}, retry:{}", receiverId, i + 1);
            }

            if (!addSuccess) {
                log.error("增加领取者余额失败, userId:{}, amount:{}", receiverId, receiveAmount);
                throw new GlobalException("领取红包失败，请联系客服");
            }

            // 创建交易流水
            walletTransactionService.createTransaction(
                    redPacket.getSenderId(), receiverId, receiveAmount,
                    TransactionTypeEnum.RED_PACKET.getCode(),
                    packetNo, "领取红包"
            );

            // 检查是否已领完
            RedPacket updatedPacket = this.getById(redPacket.getId());
            if (updatedPacket.getRemainingCount() == 0) {
                updatedPacket.setStatus(RedPacketStatusEnum.FINISHED.getCode());
                this.updateById(updatedPacket);
                
                // 标记手气最佳
                markBestLuck(updatedPacket.getId());
            }

            log.info("领取红包成功, packetNo:{}, receiverId:{}, amount:{}",
                    packetNo, receiverId, receiveAmount);

            return receiveAmount;

        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public RedPacketVO getRedPacketDetail(String packetNo) {
        LambdaQueryWrapper<RedPacket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RedPacket::getPacketNo, packetNo);
        RedPacket redPacket = this.getOne(wrapper);

        if (redPacket == null) {
            throw new GlobalException("红包不存在");
        }

        Long currentUserId = SessionContext.getSession().getUserId();
        return buildRedPacketVO(redPacket, currentUserId);
    }

    @Override
    public List<RedPacketReceiveVO> getReceiveRecords(String packetNo) {
        // 查询红包
        LambdaQueryWrapper<RedPacket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RedPacket::getPacketNo, packetNo);
        RedPacket redPacket = this.getOne(wrapper);

        if (redPacket == null) {
            throw new GlobalException("红包不存在");
        }

        // 查询领取记录
        LambdaQueryWrapper<RedPacketReceive> receiveWrapper = new LambdaQueryWrapper<>();
        receiveWrapper.eq(RedPacketReceive::getPacketId, redPacket.getId())
                     .orderByDesc(RedPacketReceive::getReceiveTime);
        List<RedPacketReceive> receives = redPacketReceiveMapper.selectList(receiveWrapper);

        if (receives.isEmpty()) {
            return List.of();
        }

        // 获取所有领取者信息
        Set<Long> receiverIds = receives.stream()
                .map(RedPacketReceive::getReceiverId)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userService.listByIds(receiverIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 转换为VO
        return receives.stream().map(receive -> {
            RedPacketReceiveVO vo = BeanUtils.copyProperties(receive, RedPacketReceiveVO.class);
            User user = userMap.get(receive.getReceiverId());
            if (user != null) {
                vo.setReceiverName(user.getNickName());
                vo.setReceiverAvatar(user.getHeadImage());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundExpiredRedPackets() {
        // 查询过期的红包
        LambdaQueryWrapper<RedPacket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RedPacket::getStatus, RedPacketStatusEnum.SENDING.getCode())
               .lt(RedPacket::getExpireTime, new Date());
        
        List<RedPacket> expiredPackets = this.list(wrapper);

        for (RedPacket redPacket : expiredPackets) {
            try {
                // 退还剩余金额给发送者
                if (redPacket.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
                    UserWallet senderWallet = userWalletService.getByUserId(redPacket.getSenderId());
                    
                    // 解冻并增加余额
                    int result = userWalletMapper.unfreezeBalance(
                            redPacket.getSenderId(),
                            redPacket.getRemainingAmount(),
                            senderWallet.getVersion()
                    );

                    if (result > 0) {
                        // 更新红包状态为已退款
                        redPacket.setStatus(RedPacketStatusEnum.REFUNDED.getCode());
                        this.updateById(redPacket);

                        log.info("红包过期退款成功, packetNo:{}, amount:{}",
                                redPacket.getPacketNo(), redPacket.getRemainingAmount());
                    }
                }
            } catch (Exception e) {
                log.error("红包过期退款失败, packetNo:{}", redPacket.getPacketNo(), e);
            }
        }
    }

    /**
     * 验证红包参数
     */
    private void validateRedPacket(SendRedPacketDTO dto) {
        // 普通红包：平均金额不能少于0.01元
        if (RedPacketTypeEnum.NORMAL.getCode().equals(dto.getPacketType())) {
            BigDecimal avgAmount = dto.getTotalAmount().divide(
                    new BigDecimal(dto.getTotalCount()), 2, RoundingMode.DOWN);
            if (avgAmount.compareTo(new BigDecimal("0.01")) < 0) {
                throw new GlobalException("红包金额太小，平均每个不能少于0.01元");
            }
        }

        // 拼手气红包：总金额至少是个数的0.01倍
        if (RedPacketTypeEnum.LUCKY.getCode().equals(dto.getPacketType())) {
            BigDecimal minAmount = new BigDecimal(dto.getTotalCount()).multiply(new BigDecimal("0.01"));
            if (dto.getTotalAmount().compareTo(minAmount) < 0) {
                throw new GlobalException("红包金额太小，至少需要" + minAmount + "元");
            }
        }
    }

    /**
     * 计算领取金额
     */
    private BigDecimal calculateReceiveAmount(RedPacket redPacket) {
        // 如果是最后一个红包，返回剩余全部金额
        if (redPacket.getRemainingCount() == 1) {
            return redPacket.getRemainingAmount();
        }

        // 普通红包：平均分配
        if (RedPacketTypeEnum.NORMAL.getCode().equals(redPacket.getPacketType())) {
            return redPacket.getRemainingAmount()
                    .divide(new BigDecimal(redPacket.getRemainingCount()), 2, RoundingMode.DOWN);
        }

        // 拼手气红包：随机金额（二倍均值法）
        BigDecimal remainingAmount = redPacket.getRemainingAmount();
        int remainingCount = redPacket.getRemainingCount();

        // 计算最大可抢金额（剩余平均值的2倍）
        BigDecimal maxAmount = remainingAmount
                .divide(new BigDecimal(remainingCount), 2, RoundingMode.DOWN)
                .multiply(new BigDecimal("2"));

        // 随机生成金额（0.01 到 maxAmount 之间）
        BigDecimal randomAmount = new BigDecimal(RandomUtil.randomDouble(0.01, maxAmount.doubleValue()))
                .setScale(2, RoundingMode.DOWN);

        // 确保至少0.01元
        if (randomAmount.compareTo(new BigDecimal("0.01")) < 0) {
            randomAmount = new BigDecimal("0.01");
        }

        // 确保剩余金额够分（至少给后面的红包留0.01元）
        BigDecimal minRemaining = new BigDecimal(remainingCount - 1).multiply(new BigDecimal("0.01"));
        if (remainingAmount.subtract(randomAmount).compareTo(minRemaining) < 0) {
            randomAmount = remainingAmount.subtract(minRemaining);
        }

        return randomAmount;
    }

    /**
     * 标记手气最佳
     */
    private void markBestLuck(Long packetId) {
        // 查询所有领取记录
        LambdaQueryWrapper<RedPacketReceive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RedPacketReceive::getPacketId, packetId)
               .orderByDesc(RedPacketReceive::getAmount);
        
        List<RedPacketReceive> receives = redPacketReceiveMapper.selectList(wrapper);
        if (!receives.isEmpty()) {
            // 标记金额最大的为手气最佳
            RedPacketReceive bestLuck = receives.get(0);
            bestLuck.setIsBestLuck(1);
            redPacketReceiveMapper.updateById(bestLuck);
        }
    }

    /**
     * 构建红包VO
     */
    private RedPacketVO buildRedPacketVO(RedPacket redPacket, Long currentUserId) {
        RedPacketVO vo = BeanUtils.copyProperties(redPacket, RedPacketVO.class);

        // 设置发送者信息
        User sender = userService.getById(redPacket.getSenderId());
        if (sender != null) {
            vo.setSenderName(sender.getNickName());
            vo.setSenderAvatar(sender.getHeadImage());
        }

        // 设置类型描述
        RedPacketTypeEnum typeEnum = RedPacketTypeEnum.fromCode(redPacket.getPacketType());
        if (typeEnum != null) {
            vo.setPacketTypeDesc(typeEnum.getDesc());
        }

        // 设置状态描述
        RedPacketStatusEnum statusEnum = RedPacketStatusEnum.fromCode(redPacket.getStatus());
        if (statusEnum != null) {
            vo.setStatusDesc(statusEnum.getDesc());
        }

        // 查询当前用户是否已领取
        if (currentUserId != null) {
            LambdaQueryWrapper<RedPacketReceive> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RedPacketReceive::getPacketId, redPacket.getId())
                   .eq(RedPacketReceive::getReceiverId, currentUserId);
            RedPacketReceive receive = redPacketReceiveMapper.selectOne(wrapper);
            
            if (receive != null) {
                vo.setReceived(true);
                vo.setReceivedAmount(receive.getAmount());
            } else {
                vo.setReceived(false);
            }
        } else {
            vo.setReceived(false);
        }

        return vo;
    }

    /**
     * 创建红包消息记录并推送
     */
    private void createAndSendRedPacketMessage(SendRedPacketDTO dto, String packetNo, UserSession session, RedPacketVO vo) {
        // chatType: 1-单聊, 2-群聊
        if (dto.getChatType() == 1) {
            // 单聊红包消息
            PrivateMessage msg = new PrivateMessage();
            msg.setSendId(session.getUserId());
            msg.setRecvId(dto.getTargetId());
            msg.setContent(packetNo); // 存储红包编号
            msg.setType(MessageType.RED_PACKET.code());
            msg.setStatus(MessageStatus.PENDING.code());
            msg.setSendTime(new Date());
            privateMessageMapper.insert(msg);
            
            // 推送消息
            PrivateMessageVO msgVO = new PrivateMessageVO();
            msgVO.setId(msg.getId());
            msgVO.setSendId(msg.getSendId());
            msgVO.setRecvId(msg.getRecvId());
            msgVO.setContent(msg.getContent());
            msgVO.setType(msg.getType());
            msgVO.setStatus(msg.getStatus());
            msgVO.setSendTime(msg.getSendTime());
            
            IMPrivateMessage<PrivateMessageVO> sendMessage = new IMPrivateMessage<>();
            sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
            sendMessage.setRecvId(dto.getTargetId());
            sendMessage.setSendToSelf(true);
            sendMessage.setData(msgVO);
            sendMessage.setSendResult(true);
            imClient.sendPrivateMessage(sendMessage);
            
            log.info("推送私聊红包消息，发送id:{}, 接收id:{}, 红包编号:{}", session.getUserId(), dto.getTargetId(), packetNo);
        } else if (dto.getChatType() == 2) {
            // 群聊红包消息
            GroupMember member = groupMemberService.findByGroupAndUserId(dto.getTargetId(), session.getUserId());
            
            GroupMessage msg = new GroupMessage();
            msg.setGroupId(dto.getTargetId());
            msg.setSendId(session.getUserId());
            msg.setSendNickName(member != null ? member.getShowNickName() : "");
            msg.setContent(packetNo); // 存储红包编号
            msg.setType(MessageType.RED_PACKET.code());
            msg.setStatus(MessageStatus.PENDING.code());
            msg.setSendTime(new Date());
            msg.setReceipt(false);
            msg.setReceiptOk(false);
            groupMessageMapper.insert(msg);
            
            // 推送消息
            GroupMessageVO msgVO = new GroupMessageVO();
            msgVO.setId(msg.getId());
            msgVO.setGroupId(msg.getGroupId());
            msgVO.setSendId(msg.getSendId());
            msgVO.setSendNickName(msg.getSendNickName());
            msgVO.setContent(msg.getContent());
            msgVO.setType(msg.getType());
            msgVO.setStatus(msg.getStatus());
            msgVO.setSendTime(msg.getSendTime());
            
            // 获取群成员列表
            List<Long> userIds = groupMemberService.findUserIdsByGroupId(dto.getTargetId());
            // 不用发给自己
            userIds = userIds.stream().filter(id -> !session.getUserId().equals(id)).collect(Collectors.toList());
            
            IMGroupMessage<GroupMessageVO> sendMessage = new IMGroupMessage<>();
            sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
            sendMessage.setRecvIds(userIds);
            sendMessage.setData(msgVO);
            imClient.sendGroupMessage(sendMessage);
            
            log.info("推送群聊红包消息，发送id:{}, 群id:{}, 红包编号:{}", session.getUserId(), dto.getTargetId(), packetNo);
        }
    }
}

