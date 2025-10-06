package com.bx.implatform.contant;

public final class Constant {

    /**
     * 系统用户id
     */
    public static final Long SYS_USER_ID = 0L;
    /**
     * 最大图片上传大小
     */
    public static final Long MAX_IMAGE_SIZE = 20 * 1024 * 1024L;
    /**
     * 最大上传文件大小
     */
    public static final Long MAX_FILE_SIZE = 20 * 1024 * 1024L;

    /**
     * 最大上传视频大小
     */
    public static final Long MAX_VIDEO_SIZE = 50 * 1024 * 1024L;
    /**
     * 大群人数上限
     */
    public static final Long MAX_LARGE_GROUP_MEMBER = 3000L;

    /**
     * 普通群人数上限
     */
    public static final Long MAX_NORMAL_GROUP_MEMBER = 500L;

    /**
     * 好友申请列表上线
     */
    public static final Long MAX_PRIEND_APPLY = 20L;

    /**
     * 扫码登录过期时间 (单位:分钟)
     */
    public static final Integer QR_LOGIN_EXPIRE_MINUTES = 2;

}
