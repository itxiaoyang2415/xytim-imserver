package com.bx.implatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bx.implatform.entity.UserWallet;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 用户钱包 Mapper 接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface UserWalletMapper extends BaseMapper<UserWallet> {

    /**
     * 扣减余额并增加冻结金额（使用乐观锁）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param version 版本号
     * @return 更新行数
     */
    int freezeBalance(@Param("userId") Long userId, 
                     @Param("amount") BigDecimal amount,
                     @Param("version") Integer version);

    /**
     * 扣减冻结金额（使用乐观锁）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param version 版本号
     * @return 更新行数
     */
    int deductFrozenBalance(@Param("userId") Long userId,
                           @Param("amount") BigDecimal amount,
                           @Param("version") Integer version);

    /**
     * 增加余额（使用乐观锁）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param version 版本号
     * @return 更新行数
     */
    int addBalance(@Param("userId") Long userId,
                  @Param("amount") BigDecimal amount,
                  @Param("version") Integer version);

    /**
     * 扣减余额（使用乐观锁）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param version 版本号
     * @return 更新行数
     */
    int deductBalance(@Param("userId") Long userId,
                     @Param("amount") BigDecimal amount,
                     @Param("version") Integer version);

    /**
     * 解冻金额并增加余额（使用乐观锁）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param version 版本号
     * @return 更新行数
     */
    int unfreezeBalance(@Param("userId") Long userId,
                       @Param("amount") BigDecimal amount,
                       @Param("version") Integer version);

    /**
     * 解冻金额并扣减余额（使用乐观锁）
     * 用于转账领取：同时减少冻结金额和可用余额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param version 版本号
     * @return 更新行数
     */
    int unfreezeAndDeductBalance(@Param("userId") Long userId,
                                 @Param("amount") BigDecimal amount,
                                 @Param("version") Integer version);
}

