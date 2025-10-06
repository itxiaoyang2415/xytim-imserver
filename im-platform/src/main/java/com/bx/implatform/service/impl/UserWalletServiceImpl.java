package com.bx.implatform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.implatform.dto.SetPayPasswordDTO;
import com.bx.implatform.dto.UpdatePayPasswordDTO;
import com.bx.implatform.entity.UserWallet;
import com.bx.implatform.enums.WalletStatusEnum;
import com.bx.implatform.exception.GlobalException;
import com.bx.implatform.mapper.UserWalletMapper;
import com.bx.implatform.service.UserWalletService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.util.BeanUtils;
import com.bx.implatform.vo.UserWalletVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户钱包服务实现
 *
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService {

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserWallet getOrCreateWallet(Long userId) {
        if (userId == null) {
            throw new GlobalException("用户ID不能为空");
        }

        // 查询钱包是否存在
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = this.getOne(wrapper);

        // 不存在则创建
        if (wallet == null) {
            wallet = new UserWallet();
            wallet.setUserId(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozenBalance(BigDecimal.ZERO);
            wallet.setCurrency("CNY");
            wallet.setWalletStatus(WalletStatusEnum.NORMAL.getCode());
            wallet.setSecurityLevel(1);
            wallet.setVersion(0);
            this.save(wallet);
            log.info("创建用户钱包成功, userId:{}", userId);
        }

        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPayPassword(SetPayPasswordDTO dto) {
        // 验证两次密码是否一致
        if (!dto.getPayPassword().equals(dto.getConfirmPayPassword())) {
            throw new GlobalException("两次输入的密码不一致");
        }

        Long userId = SessionContext.getSession().getUserId();
        
        // 获取或创建钱包
        UserWallet wallet = this.getOrCreateWallet(userId);

        // 检查是否已设置支付密码
        if (StrUtil.isNotBlank(wallet.getPayPassword())) {
            throw new GlobalException("支付密码已设置，如需修改请使用修改密码接口");
        }

        // 加密密码
        String encryptedPassword = passwordEncoder.encode(dto.getPayPassword());
        
        // 使用LambdaUpdateWrapper更新，避免乐观锁问题
        LambdaUpdateWrapper<UserWallet> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserWallet::getUserId, userId)
                     .set(UserWallet::getPayPassword, encryptedPassword);
        this.update(updateWrapper);
        
        log.info("设置支付密码成功, userId:{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePayPassword(UpdatePayPasswordDTO dto) {
        // 验证新密码和确认密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new GlobalException("两次输入的新密码不一致");
        }

        // 验证新密码不能与旧密码相同
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new GlobalException("新密码不能与旧密码相同");
        }

        Long userId = SessionContext.getSession().getUserId();
        
        // 获取用户钱包
        UserWallet wallet = this.getByUserId(userId);

        // 检查是否已设置支付密码
        if (StrUtil.isBlank(wallet.getPayPassword())) {
            throw new GlobalException("尚未设置支付密码，请先设置支付密码");
        }

        // 验证钱包状态
        if (!WalletStatusEnum.NORMAL.getCode().equals(wallet.getWalletStatus())) {
            throw new GlobalException("钱包状态异常，无法修改支付密码");
        }

        // 验证旧密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), wallet.getPayPassword())) {
            throw new GlobalException("旧密码错误");
        }

        // 加密新密码
        String encryptedPassword = passwordEncoder.encode(dto.getNewPassword());
        
        // 使用LambdaUpdateWrapper更新密码
        LambdaUpdateWrapper<UserWallet> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserWallet::getUserId, userId)
                     .set(UserWallet::getPayPassword, encryptedPassword);
        this.update(updateWrapper);
        
        log.info("修改支付密码成功, userId:{}", userId);
    }

    @Override
    public boolean verifyPayPassword(Long userId, String payPassword) {
        if (userId == null || StrUtil.isBlank(payPassword)) {
            return false;
        }

        UserWallet wallet = this.getByUserId(userId);
        if (wallet == null || StrUtil.isBlank(wallet.getPayPassword())) {
            throw new GlobalException("请先设置支付密码");
        }

        // 验证钱包状态
        if (!WalletStatusEnum.NORMAL.getCode().equals(wallet.getWalletStatus())) {
            throw new GlobalException("钱包状态异常，无法操作");
        }

        // 验证密码
        return passwordEncoder.matches(payPassword, wallet.getPayPassword());
    }

    @Override
    public UserWalletVO getWalletInfo() {
        Long userId = SessionContext.getSession().getUserId();
        UserWallet wallet = this.getOrCreateWallet(userId);

        UserWalletVO vo = BeanUtils.copyProperties(wallet, UserWalletVO.class);
        vo.setHasPayPassword(StrUtil.isNotBlank(wallet.getPayPassword()));
        return vo;
    }

    @Override
    public UserWallet getByUserId(Long userId) {
        if (userId == null) {
            throw new GlobalException("用户ID不能为空");
        }

        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = this.getOne(wrapper);

        if (wallet == null) {
            throw new GlobalException("钱包不存在");
        }

        return wallet;
    }
}

