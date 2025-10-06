package com.bx.implatform.contant;

public final class RedisKey {

    /**
     * 用户状态 无值:空闲  1:正在忙
     */
    public static final String IM_USER_STATE = "im:user:state";

    /**
     * 用户cid
     */
    public static final String IM_USER_CID = "im:user:cid";

    /**
     * 验证码
     */
    public static final String IM_CAPTCHA = "im:captcha";

    /**
     * 图形验证码
     */
    public static final String IM_CAPTCHA_IMAGE = "im:captcha:img";

    /**
     * 短信验证码
     */
    public static final String IM_CAPTCHA_SMS = "im:captcha:sms";

    /**
     * 邮箱验证码
     */
    public static final String IM_CAPTCHA_MAIL = "im:captcha:mail";

    /**
     * 已读群聊消息位置(已读最大id)
     */
    public static final String IM_GROUP_READED_POSITION = "im:readed:group:position";

    /**
     * 已读系统消息位置(已读最大seqNo)
     */
    public static final String IM_SYSTEM_READED_POSITION = "im:readed:system:position";

    /**
     * 群成员最大版本号
     */
    public static final String IM_GROUP_MEMBER_MAX_VERSION = "im:group:member:max_version";

    /**
     * 离线通知
     */
    public static final String IM_NOTIFY_OFFLINE_SESSION = "im:notify:offline";

    /**
     * 系统消息推送序列号
     */
    public static final String IM_SM_TASK_SEQ = "im:task:sm:seq";
    /**
     * webrtc 单人通话
     */
    public static final String IM_WEBRTC_PRIVATE_SESSION = "im:webrtc:private:session";
    /**
     * webrtc 群通话
     */
    public static final String IM_WEBRTC_GROUP_SESSION = "im:webrtc:group:session";

    /**
     * 用户被封禁消息队列
     */
    public static final String IM_QUEUE_USER_BANNED = "im:queue:user:banned";

    /**
     * 群聊被封禁消息队列
     */
    public static final String IM_QUEUE_GROUP_BANNED = "im:queue:group:banned";

    /**
     * 群聊解封消息队列
     */
    public static final String IM_QUEUE_GROUP_UNBAN = "im:queue:group:unban";

    /**
     * 缓存是否好友：bool
     */
    public static final String IM_CACHE_FRIEND = "im:cache:friend";

    /**
     * 缓存好友免打扰
     */
    public static final String IM_CACHE_FRIEND_DND = "im:cache:friend_dnd";

    /**
     * 缓存群聊信息
     */
    public static final String IM_CACHE_GROUP = "im:cache:group";

    /**
     * 缓存群聊免打扰
     */
    public static final String IM_CACHE_GROUP_DNS = "im:cache:group_dnd";

    /**
     * 缓存是否在黑名单中：bool
     */
    public static final String IM_CACHE_BLACKLIST = "im:cache:blacklist";
    /**
     * 缓存群聊成员id
     */
    public static final String IM_CACHE_GROUP_MEMBER_ID = "im:cache:group_member_ids";

    /**
     * 分布式锁-群通话
     */
    public static final String IM_LOCK_RTC_GROUP = "im:lock:rtc:group";
    /**
     * 分布式锁-系统消息推送
     */
    public static final String IM_LOCK_SM_TASK = "im:lock:task:sm";

    /**
     * 分布式锁-清理过期文件
     */
    public static final String IM_LOCK_FILE_EXPIRE_TASK = "im:lock:task:file:expire";

    /**
     * 分布式锁-清理无效文件
     */
    public static final String IM_LOCK_FILE_INVALID_TASK = "im:lock:task:file:invalid" + "";
    /**
     * 分布式锁-添加好友
     */
    public static final String IM_LOCK_FRIEND_ADD = "im:lock:friend:add";

    /**
     * 分布式锁-进入群聊
     */
    public static final String IM_LOCK_GROUP_ENTER = "im:lock:group:enter";

    /**
     * 重复提交
     */
    public static final String IM_REPEAT_SUBMIT = "im:repeat:submit";

    /**
     * 登陆二维码
     */
    public static final String IM_LOGIN_QRCODE = "im:login:qrcode";

    /**
     * app是否正在审核中
     */
    public static final String IM_APP_REVIEW = "im:app:review";

}
