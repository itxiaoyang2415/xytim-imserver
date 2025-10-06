package com.bx.implatform.aspect;

import com.bx.implatform.annotation.PrivacyProtected;
import com.bx.implatform.entity.privacy.GroupVirtualMember;
import com.bx.implatform.service.privacy.GroupPrivacyService;
import com.bx.implatform.service.privacy.VirtualMemberService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 群组隐私保护切面
 * 
 * 拦截带有 @PrivacyProtected 注解的方法，自动将返回结果中的用户ID和昵称替换为虚拟信息
 * 
 * @author blue
 * @since 2025-10-06
 */
@Slf4j
@Aspect
@Order(100)
@Component
@RequiredArgsConstructor
public class GroupPrivacyAspect {

    private final GroupPrivacyService groupPrivacyService;
    private final VirtualMemberService virtualMemberService;

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(privacyProtected)")
    public Object around(ProceedingJoinPoint joinPoint, PrivacyProtected privacyProtected) throws Throwable {
        // 执行原方法
        Object result = joinPoint.proceed();

        try {
            // 检查全局开关
            if (!groupPrivacyService.isGlobalPrivacyEnabled()) {
                return result;
            }

            // 获取群组ID
            Long groupId = extractGroupId(joinPoint, result, privacyProtected);
            if (groupId == null) {
                log.warn("无法提取群组ID，跳过隐私保护");
                return result;
            }

            // 检查群组隐私配置
            if (!groupPrivacyService.isPrivacyEnabled(groupId)) {
                return result;
            }

            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                log.warn("无法获取当前用户ID，跳过隐私保护");
                return result;
            }

            // 检查当前用户权限
            if (groupPrivacyService.canViewRealInfo(groupId, currentUserId)) {
                // 管理员可以看真实信息，不需要转换
                return result;
            }

            // 处理返回结果，替换用户ID和昵称
            processResult(result, groupId, privacyProtected);

        } catch (Exception e) {
            log.error("群组隐私保护处理失败", e);
            // 出现异常时，不影响正常业务流程
        }

        return result;
    }

    /**
     * 提取群组ID
     */
    private Long extractGroupId(ProceedingJoinPoint joinPoint, Object result, PrivacyProtected annotation) {
        try {
            String groupIdParam = annotation.groupIdParam();

            if (annotation.fromResult()) {
                // 从返回结果中提取
                return extractFromObject(result, groupIdParam);
            } else {
                // 从方法参数中提取
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String[] parameterNames = signature.getParameterNames();
                Object[] args = joinPoint.getArgs();

                if (parameterNames == null || parameterNames.length == 0) {
                    return null;
                }

                // 如果是简单参数名
                for (int i = 0; i < parameterNames.length; i++) {
                    if (groupIdParam.equals(parameterNames[i])) {
                        return convertToLong(args[i]);
                    }
                }

                // 如果是SpEL表达式
                if (groupIdParam.startsWith("#")) {
                    EvaluationContext context = new StandardEvaluationContext();
                    for (int i = 0; i < parameterNames.length; i++) {
                        context.setVariable(parameterNames[i], args[i]);
                    }
                    Object value = parser.parseExpression(groupIdParam).getValue(context);
                    return convertToLong(value);
                }
            }
        } catch (Exception e) {
            log.error("提取群组ID失败", e);
        }
        return null;
    }

    /**
     * 从对象中提取值
     */
    private Long extractFromObject(Object obj, String path) {
        try {
            String[] parts = path.split("\\.");
            Object current = obj;

            for (String part : parts) {
                if (current == null) {
                    return null;
                }
                Field field = current.getClass().getDeclaredField(part);
                field.setAccessible(true);
                current = field.get(current);
            }

            return convertToLong(current);
        } catch (Exception e) {
            log.error("从对象中提取值失败, path: {}", path, e);
        }
        return null;
    }

    /**
     * 转换为Long
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            UserSession session = SessionContext.getSession();
            return session != null ? session.getUserId() : null;
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
            return null;
        }
    }

    /**
     * 处理返回结果
     */
    private void processResult(Object result, Long groupId, PrivacyProtected annotation) {
        if (result == null) {
            return;
        }

        // 解析字段映射
        String[] fieldMappings = annotation.fieldMapping();
        if (fieldMappings == null || fieldMappings.length == 0) {
            return;
        }

        for (String mapping : fieldMappings) {
            String[] parts = mapping.split(":");
            if (parts.length != 2) {
                continue;
            }

            String userIdField = parts[0].trim();
            String nickNameField = parts[1].trim();

            // 处理对象
            processObject(result, groupId, userIdField, nickNameField);
        }
    }

    /**
     * 处理对象
     */
    private void processObject(Object obj, Long groupId, String userIdField, String nickNameField) {
        if (obj == null) {
            return;
        }

        // 如果是ResultUtils包装的结果
        if (obj.getClass().getName().contains("ResultUtils") || obj.getClass().getName().contains("Result")) {
            try {
                Field dataField = obj.getClass().getDeclaredField("data");
                dataField.setAccessible(true);
                Object data = dataField.get(obj);
                processObject(data, groupId, userIdField, nickNameField);
                return;
            } catch (Exception e) {
                // 继续处理
            }
        }

        // 如果是集合
        if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            for (Object item : collection) {
                processObject(item, groupId, userIdField, nickNameField);
            }
            return;
        }

        // 如果是数组
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            for (Object item : array) {
                processObject(item, groupId, userIdField, nickNameField);
            }
            return;
        }

        // 处理单个对象
        processSingleObject(obj, groupId, userIdField, nickNameField);
    }

    /**
     * 处理单个对象
     */
    private void processSingleObject(Object obj, Long groupId, String userIdField, String nickNameField) {
        try {
            // 获取真实用户ID
            Long realUserId = getFieldValue(obj, userIdField, Long.class);
            if (realUserId == null) {
                return;
            }

            // 获取虚拟成员信息
            GroupVirtualMember virtualMember = virtualMemberService.getVirtualMember(groupId, realUserId);
            if (virtualMember == null) {
                return;
            }

            // 替换用户ID
            setFieldValue(obj, userIdField, virtualMember.getVirtualUserId());

            // 替换昵称
            setFieldValue(obj, nickNameField, virtualMember.getVirtualNickName());

            // 如果有isReal字段，设置为false
            try {
                setFieldValue(obj, "isReal", false);
            } catch (Exception e) {
                // 忽略，字段可能不存在
            }

            log.debug("替换用户信息: realUserId={} -> virtualUserId={}, virtualNickName={}",
                    realUserId, virtualMember.getVirtualUserId(), virtualMember.getVirtualNickName());

        } catch (Exception e) {
            log.error("处理单个对象失败", e);
        }
    }

    /**
     * 获取字段值
     */
    private <T> T getFieldValue(Object obj, String fieldPath, Class<T> type) throws Exception {
        String[] parts = fieldPath.split("\\.");
        Object current = obj;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            // 处理集合
            if (part.endsWith("[]")) {
                String fieldName = part.substring(0, part.length() - 2);
                Field field = findField(current.getClass(), fieldName);
                if (field == null) {
                    return null;
                }
                field.setAccessible(true);
                current = field.get(current);
                if (current instanceof Collection) {
                    // 对于集合，我们在上层处理
                    return null;
                }
            } else {
                Field field = findField(current.getClass(), part);
                if (field == null) {
                    return null;
                }
                field.setAccessible(true);
                current = field.get(current);
            }
        }

        return type.cast(current);
    }

    /**
     * 设置字段值
     */
    private void setFieldValue(Object obj, String fieldPath, Object value) throws Exception {
        String[] parts = fieldPath.split("\\.");
        Object current = obj;

        // 导航到最后一个对象
        for (int i = 0; i < parts.length - 1; i++) {
            Field field = findField(current.getClass(), parts[i]);
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            current = field.get(current);
            if (current == null) {
                return;
            }
        }

        // 设置最后一个字段
        String lastFieldName = parts[parts.length - 1];
        Field field = findField(current.getClass(), lastFieldName);
        if (field != null) {
            field.setAccessible(true);
            field.set(current, value);
        }
    }

    /**
     * 查找字段（包括父类）
     */
    private Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            }
        }
        return null;
    }
}

