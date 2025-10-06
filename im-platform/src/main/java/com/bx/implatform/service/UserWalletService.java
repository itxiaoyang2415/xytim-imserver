package com.bx.implatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.implatform.dto.SetPayPasswordDTO;
import com.bx.implatform.dto.UpdatePayPasswordDTO;
import com.bx.implatform.entity.UserWallet;
import com.bx.implatform.vo.UserWalletVO;

/**
 * 用户钱包服务接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface UserWalletService extends IService<UserWallet> {

    /**
     * 获取或创建用户钱包
     *
     * @param userId 用户ID
     * @return 用户钱包
     */
    UserWallet getOrCreateWallet(Long userId);

    /**
     * 设置支付密码
     *
     * @param dto 支付密码DTO
     */
    void setPayPassword(SetPayPasswordDTO dto);

    /**
     * 修改支付密码
     *
     * @param dto 修改支付密码DTO
     */
    void updatePayPassword(UpdatePayPasswordDTO dto);

    /**
     * 验证支付密码
     *
     * @param userId 用户ID
     * @param payPassword 支付密码
     * @return 是否正确
     */
    boolean verifyPayPassword(Long userId, String payPassword);

    /**
     * 查询用户钱包信息
     *
     * @return 钱包VO
     */
    UserWalletVO getWalletInfo();

    /**
     * 根据用户ID查询钱包
     *
     * @param userId 用户ID
     * @return 用户钱包
     */
    UserWallet getByUserId(Long userId);
}

