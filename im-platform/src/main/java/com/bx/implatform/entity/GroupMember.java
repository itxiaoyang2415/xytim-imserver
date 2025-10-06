package com.bx.implatform.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 群成员
 * </p>
 *
 * @author blue
 * @since 2022-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_group_member")
public class GroupMember extends Model<GroupMember> {

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 群id
     */
    private Long groupId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickName;

    /**
     * 显示昵称备注
     */
    private String remarkNickName;

    /**
     * 用户头像
     */
    private String headImage;

    /**
     * 显示群名备注
     */
    private String remarkGroupName;

    /**
     * 成员所属企业名称
     */
    private String companyName;

    /**
     * 是否管理员
     */
    private Boolean isManager;

    /**
     * 是否被禁言
     */
    private Boolean isMuted;

    /**
     * 是否免打扰
     */
    private Boolean isDnd;


    /**
     * 是否显示置顶消息
     */
    private Boolean isTopMessage;

    /**
     * 是否置顶会话
     */
    private Boolean isTop;

    /**
     * 是否已退出
     */
    private Boolean quit;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 退出时间
     */
    private Date quitTime;

    /**
     * 版本号
     */
    private Long version;

    public String getShowNickName() {
        return StrUtil.blankToDefault(remarkNickName, userNickName);
    }

}
