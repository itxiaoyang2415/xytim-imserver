package com.bx.implatform.annotation;

import java.lang.annotation.*;

/**
 * 群组隐私保护注解
 * 
 * 用于标记需要进行隐私保护的方法
 * 当方法执行完毕后，AOP会自动将返回结果中的用户ID和昵称替换为虚拟信息
 * 
 * 使用场景：
 * 1. 查询群成员列表
 * 2. 查询群成员详情
 * 3. 查询群消息列表（消息中包含发送者信息）
 * 
 * @author blue
 * @since 2025-10-06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrivacyProtected {

    /**
     * 群组ID的参数名称
     * 默认为 "groupId"
     * 
     * 支持SpEL表达式，例如：
     * - "groupId" : 直接参数
     * - "#dto.groupId" : DTO中的属性
     * - "#result.data.groupId" : 返回值中的属性
     */
    String groupIdParam() default "groupId";

    /**
     * 是否从返回结果中获取groupId
     * 默认为false（从方法参数中获取）
     * 
     * 如果设置为true，将从返回结果中解析groupId
     */
    boolean fromResult() default false;

    /**
     * 需要处理的字段映射
     * 格式：userIdField:nickNameField
     * 
     * 例如：
     * - "userId:nickName" : 单个对象
     * - "data.userId:data.nickName" : 嵌套对象
     * - "list[].userId:list[].nickName" : 列表对象
     */
    String[] fieldMapping() default {"userId:nickName"};
}

