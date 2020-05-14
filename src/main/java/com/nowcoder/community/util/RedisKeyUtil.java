package com.nowcoder.community.util;

/** 生成 redis key的工具；
 * 这是一个简单的工具，不需要注入容器管理，提供静态方法访问即可
 * @author bing  @create 2020/5/13 2:25 下午
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":"; //所以key拼接时用 ： 来分隔
    private static final String PREFIX_ENTITY_LIKE = "like:entity";//实体为key 赞前缀
    private static final String PREFIX_USER_LIKE = "like:user";//以用户为key 赞前缀
    private static final String PREFIX_FOLLOWEE = "followee";//统计关注的实体
    private static final String PREFIX_FOLLOWER = "follower";//统计实体的粉丝
    private static final String PREFIX_KAPTCHA = "kaptcha";// 定义存验证码的前缀
    private static final String PREFIX_TICKET = "ticket";// 定义存登录凭证的前缀
    private static final String PREFIX_USER = "user";// 定义存用户信息的前缀


    //某个实体的赞
    // like:entity:entityType:entityId -> set（userId)(存到一个集合，方便查询各种数据）
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体; 被关注的; userId 表示谁关注；entityId 实体id;
    // followee:userId:entityType -> zset(entityId,now)有序集合
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)以时间作为分数排序
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登录证码的key
    // 在用户访问登录页面的时候，给用户发一个随机字符串，存到cookie，以这个字符串来标识这个用户
    // owner 用户临时凭证
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}





























