package com.bx.implatform.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.imclient.IMClient;
import com.bx.imcommon.contant.IMConstant;
import com.bx.imcommon.enums.IMTerminalType;
import com.bx.imcommon.model.IMPrivateMessage;
import com.bx.imcommon.model.IMUserInfo;
import com.bx.imcommon.util.ThreadPoolExecutorFactory;
import com.bx.implatform.annotation.OnlineCheck;
import com.bx.implatform.dto.PrivateMessageDTO;
import com.bx.implatform.entity.PrivateMessage;
import com.bx.implatform.enums.MessageStatus;
import com.bx.implatform.enums.MessageType;
import com.bx.implatform.exception.GlobalException;
import com.bx.implatform.mapper.PrivateMessageMapper;
import com.bx.implatform.service.FriendService;
import com.bx.implatform.service.PrivateMessageService;
import com.bx.implatform.service.UserBlacklistService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.session.UserSession;
import com.bx.implatform.util.BeanUtils;
import com.bx.implatform.util.SensitiveFilterUtil;
import com.bx.implatform.vo.PrivateMessageVO;
import com.bx.implatform.vo.QuoteMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage>
    implements PrivateMessageService {

    private final FriendService friendService;
    private final UserBlacklistService userBlacklistService;
    private final IMClient imClient;
    private final SensitiveFilterUtil sensitiveFilterUtil;
    private static final ScheduledThreadPoolExecutor EXECUTOR = ThreadPoolExecutorFactory.getThreadPoolExecutor();

    @Override
    public PrivateMessageVO sendMessage(PrivateMessageDTO dto) {
        UserSession session = SessionContext.getSession();
        if (!friendService.isFriend(session.getUserId(), dto.getRecvId())) {
            throw new GlobalException("您已不是对方好友，无法发送消息");
        }
        if (userBlacklistService.isInBlacklist(dto.getRecvId(), session.getUserId())) {
            throw new GlobalException("对方已将您拉入黑名单，无法发送消息");
        }
        // 保存消息
        PrivateMessage msg = BeanUtils.copyProperties(dto, PrivateMessage.class);
        msg.setSendId(session.getUserId());
        msg.setStatus(MessageStatus.PENDING.code());
        msg.setSendTime(new Date());
        // 过滤内容中的敏感词
        if (MessageType.TEXT.code().equals(dto.getType())) {
            msg.setContent(sensitiveFilterUtil.filter(dto.getContent()));
        }
        this.save(msg);
        // 推送消息
        PrivateMessageVO msgInfo = BeanUtils.copyProperties(msg, PrivateMessageVO.class);
        // 填充引用消息
        if (!Objects.isNull(dto.getQuoteMessageId())) {
            PrivateMessage quoteMessage = this.getById(dto.getQuoteMessageId());
            msgInfo.setQuoteMessage(BeanUtils.copyProperties(quoteMessage, QuoteMessageVO.class));
            // 防止显示已撤回的内容
            if (quoteMessage.getStatus().equals(MessageStatus.RECALL.code())) {
                msgInfo.getQuoteMessage().setContent("引用内容已撤回");
            }
        }
        IMPrivateMessage<PrivateMessageVO> sendMessage = new IMPrivateMessage<>();
        sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(msgInfo.getRecvId());
        sendMessage.setSendToSelf(true);
        sendMessage.setData(msgInfo);
        sendMessage.setSendResult(true);
        imClient.sendPrivateMessage(sendMessage);
        log.info("发送私聊消息，发送id:{},接收id:{}，内容:{}", session.getUserId(), dto.getRecvId(), dto.getContent());
        return msgInfo;
    }

    @Transactional
    @Override
    public PrivateMessageVO recallMessage(Long id) {
        UserSession session = SessionContext.getSession();
        PrivateMessage msg = this.getById(id);
        if (Objects.isNull(msg)) {
            throw new GlobalException("消息不存在");
        }
        if (!msg.getSendId().equals(session.getUserId())) {
            throw new GlobalException("这条消息不是由您发送,无法撤回");
        }
        if (System.currentTimeMillis() - msg.getSendTime().getTime() > IMConstant.ALLOW_RECALL_SECOND * 1000) {
            throw new GlobalException("消息已发送超过5分钟，无法撤回");
        }
        // 修改消息状态
        msg.setStatus(MessageStatus.RECALL.code());
        this.updateById(msg);
        // 生成一条撤回消息
        PrivateMessage recallMsg = new PrivateMessage();
        recallMsg.setSendId(session.getUserId());
        recallMsg.setStatus(MessageStatus.PENDING.code());
        recallMsg.setSendTime(new Date());
        recallMsg.setRecvId(msg.getRecvId());
        recallMsg.setType(MessageType.RECALL.code());
        recallMsg.setContent(id.toString());
        this.save(recallMsg);
        // 推送消息
        PrivateMessageVO msgInfo = BeanUtils.copyProperties(recallMsg, PrivateMessageVO.class);
        IMPrivateMessage<PrivateMessageVO> sendMessage = new IMPrivateMessage<>();
        sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(msgInfo.getRecvId());
        sendMessage.setData(msgInfo);
        imClient.sendPrivateMessage(sendMessage);
        log.info("撤回私聊消息，发送id:{},接收id:{}，内容:{}", msg.getSendId(), msg.getRecvId(), msg.getContent());
        return msgInfo;
    }


    @OnlineCheck
    @Override
    public void pullOfflineMessage(Long minId) {
        UserSession session = SessionContext.getSession();
        // 获取当前用户的消息
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        // 只能拉取最近3个月的消息,移动端只拉取一个月消息
        int months = session.getTerminal().equals(IMTerminalType.APP.code()) ? 1 : 3;
        Date minDate = DateUtils.addMonths(new Date(), -months);
        wrapper.gt(PrivateMessage::getId, minId);
        wrapper.ge(PrivateMessage::getSendTime, minDate);
        wrapper.and(wp -> wp.eq(PrivateMessage::getSendId, session.getUserId()).or()
            .eq(PrivateMessage::getRecvId, session.getUserId()));
        wrapper.orderByAsc(PrivateMessage::getId);
        List<PrivateMessage> messages = this.list(wrapper);
        // 提取所有引用消息
        Map<Long, QuoteMessageVO> quoteMessageMap = batchLoadQuoteMessage(messages);
        // 异步推送消息
        EXECUTOR.execute(() -> {
            // 开启加载中标志
            this.sendLoadingMessage(true, session);
            for (PrivateMessage m : messages) {
                // 推送过程如果用户下线了，则不再推送
                if (!imClient.isOnline(session.getUserId(), IMTerminalType.fromCode(session.getTerminal()))) {
                    log.info("用户已下线，停止推送离线私聊消息,用户id:{}", session.getUserId());
                    return;
                }
                PrivateMessageVO vo = BeanUtils.copyProperties(m, PrivateMessageVO.class);
                vo.setQuoteMessage(quoteMessageMap.get(m.getQuoteMessageId()));
                IMPrivateMessage<PrivateMessageVO> sendMessage = new IMPrivateMessage<>();
                sendMessage.setSender(new IMUserInfo(m.getSendId(), IMTerminalType.WEB.code()));
                sendMessage.setRecvId(session.getUserId());
                sendMessage.setRecvTerminals(List.of(session.getTerminal()));
                sendMessage.setSendToSelf(false);
                sendMessage.setData(vo);
                sendMessage.setSendResult(true);
                imClient.sendPrivateMessage(sendMessage);
            }
            // 关闭加载中标志
            this.sendLoadingMessage(false, session);
            log.info("拉取私聊消息，用户id:{},数量:{},minId:{}", session.getUserId(), messages.size(), minId);
        });
    }

    @Override
    public List<PrivateMessageVO> loadOfflineMessage(Long minId) {
        long time = System.currentTimeMillis();
        UserSession session = SessionContext.getSession();
        // 获取当前用户的消息
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        // 只能拉取最近1个月的消息
        Date minDate = DateUtils.addMonths(new Date(), -1);
        wrapper.gt(PrivateMessage::getId, minId);
        wrapper.ge(PrivateMessage::getSendTime, minDate);
        wrapper.and(wp -> wp.eq(PrivateMessage::getSendId, session.getUserId()).or()
            .eq(PrivateMessage::getRecvId, session.getUserId()));
        wrapper.orderByAsc(PrivateMessage::getId);
        List<PrivateMessage> messages = this.list(wrapper);
        // 更新消息为送达状态
        List<Long> messageIds =
            messages.stream().filter(m -> m.getStatus().equals(MessageStatus.PENDING.code())).map(PrivateMessage::getId)
                .collect(Collectors.toList());
        if (!messageIds.isEmpty()) {
            LambdaUpdateWrapper<PrivateMessage> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.in(PrivateMessage::getId, messageIds);
            updateWrapper.set(PrivateMessage::getStatus, MessageStatus.DELIVERED.code());
            update(updateWrapper);
        }
        // 提取所有引用消息
        Map<Long, QuoteMessageVO> quoteMessageMap = batchLoadQuoteMessage(messages);
        // 转换vo
        List<PrivateMessageVO> vos = messages.stream().map(m -> {
            PrivateMessageVO vo = BeanUtils.copyProperties(m, PrivateMessageVO.class);
            vo.setQuoteMessage(quoteMessageMap.get(m.getQuoteMessageId()));
            return vo;
        }).collect(Collectors.toList());
        log.info("拉取离线私聊消息,用户id:{},数量:{},耗时:{},minId:{}", session.getUserId(), vos.size(),
            System.currentTimeMillis() - time, minId);
        return vos;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void readedMessage(Long friendId) {
        UserSession session = SessionContext.getSession();
        // 推送消息给自己，清空会话列表上的已读数量
        PrivateMessageVO msgInfo = new PrivateMessageVO();
        msgInfo.setType(MessageType.READED.code());
        msgInfo.setSendId(session.getUserId());
        msgInfo.setRecvId(friendId);
        IMPrivateMessage<PrivateMessageVO> sendMessage = new IMPrivateMessage<>();
        sendMessage.setData(msgInfo);
        sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setSendToSelf(true);
        sendMessage.setSendResult(false);
        imClient.sendPrivateMessage(sendMessage);
        // 推送回执消息给对方，更新已读状态
        msgInfo = new PrivateMessageVO();
        msgInfo.setType(MessageType.RECEIPT.code());
        msgInfo.setSendId(session.getUserId());
        msgInfo.setRecvId(friendId);
        sendMessage = new IMPrivateMessage<>();
        sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(friendId);
        sendMessage.setSendToSelf(false);
        sendMessage.setSendResult(false);
        sendMessage.setData(msgInfo);
        imClient.sendPrivateMessage(sendMessage);
        // 修改消息状态为已读
        LambdaUpdateWrapper<PrivateMessage> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(PrivateMessage::getSendId, friendId);
        updateWrapper.eq(PrivateMessage::getRecvId, session.getUserId());
        updateWrapper.eq(PrivateMessage::getStatus, MessageStatus.DELIVERED.code());
        updateWrapper.set(PrivateMessage::getStatus, MessageStatus.READED.code());
        this.update(updateWrapper);
        log.info("消息已读，接收方id:{},发送方id:{}", session.getUserId(), friendId);
    }

    @Override
    public Long getMaxReadedId(Long friendId) {
        UserSession session = SessionContext.getSession();
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(PrivateMessage::getSendId, session.getUserId()).eq(PrivateMessage::getRecvId, friendId)
            .eq(PrivateMessage::getStatus, MessageStatus.READED.code()).orderByDesc(PrivateMessage::getId)
            .select(PrivateMessage::getId).last("limit 1");
        PrivateMessage message = this.getOne(wrapper);
        if (Objects.isNull(message)) {
            return -1L;
        }
        return message.getId();
    }

    private void sendLoadingMessage(Boolean isLoading, UserSession session) {
        PrivateMessageVO msgInfo = new PrivateMessageVO();
        msgInfo.setType(MessageType.LOADING.code());
        msgInfo.setContent(isLoading.toString());
        IMPrivateMessage<PrivateMessageVO> sendMessage = new IMPrivateMessage<>();
        sendMessage.setSender(new IMUserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(session.getUserId());
        sendMessage.setRecvTerminals(List.of(session.getTerminal()));
        sendMessage.setData(msgInfo);
        sendMessage.setSendToSelf(false);
        sendMessage.setSendResult(false);
        imClient.sendPrivateMessage(sendMessage);
    }

    private Map<Long, QuoteMessageVO> batchLoadQuoteMessage(List<PrivateMessage> messages) {
        // 提取列表中所有引用消息
        List<Long> ids =
            messages.stream().filter(m -> !Objects.isNull(m.getQuoteMessageId())).map(PrivateMessage::getQuoteMessageId)
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        wrapper.in(PrivateMessage::getId, ids);
        List<PrivateMessage> quoteMessages = this.list(wrapper);
        // 转为vo
        return quoteMessages.stream()
            .collect(Collectors.toMap(m -> m.getId(), m -> BeanUtils.copyProperties(m, QuoteMessageVO.class)));
    }
}
