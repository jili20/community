package com.nowcoder.community.util;

/** 常量接口
 * @author bing  @create 2020/5/9 10:01 上午
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    Integer ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    Integer ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    Integer ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12; // 一小时 3600秒，12小时

    /**
     * 记住我状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100; // 100 天

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;
}






















