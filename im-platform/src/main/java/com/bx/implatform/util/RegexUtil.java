package com.bx.implatform.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则校验
 * @author Blue
 * @version 1.0
 * @date 2025-03-21
 */
public class RegexUtil {
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static Boolean isPhone(String text){
        // 编译正则表达式
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        // 创建matcher对象
        Matcher matcher = pattern.matcher(text);
        // 进行匹配检查
        return matcher.matches();
    }

    public static Boolean isEmail(String text){
        // 编译正则表达式
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        // 创建matcher对象
        Matcher matcher = pattern.matcher(text);
        // 进行匹配检查
        return matcher.matches();
    }
}
