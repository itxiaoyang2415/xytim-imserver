package com.bx.implatform.mapper.privacy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bx.implatform.entity.privacy.GroupVirtualMember;
import org.apache.ibatis.annotations.Param;

/**
 * 群组模拟用户 Mapper 接口
 *
 * @author blue
 * @since 2025-10-06
 */
public interface GroupVirtualMemberMapper extends BaseMapper<GroupVirtualMember> {

    /**
     * 获取群组中下一个显示序号
     *
     * @param groupId 群组ID
     * @return 下一个显示序号
     */
    Integer getNextDisplayOrder(@Param("groupId") Long groupId);

    /**
     * 批量失效群组成员的虚拟信息
     *
     * @param groupId 群组ID
     * @return 更新行数
     */
    int invalidateByGroupId(@Param("groupId") Long groupId);
}

