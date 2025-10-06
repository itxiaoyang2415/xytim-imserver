package com.bx.implatform.service.privacy;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.implatform.dto.privacy.GroupPrivacyConfigDTO;
import com.bx.implatform.entity.privacy.GroupPrivacyConfig;
import com.bx.implatform.vo.privacy.GroupPrivacyConfigVO;

/**
 * 群组隐私服务接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface GroupPrivacyService extends IService<GroupPrivacyConfig> {

    /**
     * 配置群组隐私保护
     *
     * @param dto 配置DTO
     * @return 配置VO
     */
    GroupPrivacyConfigVO configurePrivacy(GroupPrivacyConfigDTO dto);

    /**
     * 查询群组隐私配置
     *
     * @param groupId 群组ID
     * @return 配置VO
     */
    GroupPrivacyConfigVO getGroupPrivacyConfig(Long groupId);

    /**
     * 判断群组是否开启隐私保护
     *
     * @param groupId 群组ID
     * @return 是否开启
     */
    boolean isPrivacyEnabled(Long groupId);

    /**
     * 判断用户是否有权限查看真实信息
     *
     * @param groupId 群组ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean canViewRealInfo(Long groupId, Long userId);

    /**
     * 判断全局隐私保护功能是否开启
     *
     * @return 是否开启
     */
    boolean isGlobalPrivacyEnabled();
}

