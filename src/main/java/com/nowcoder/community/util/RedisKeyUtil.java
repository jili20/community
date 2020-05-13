package com.nowcoder.community.util;

/** 生成 redis key的工具；
 * 这是一个简单的工具，不需要注入容器管理，提供静态方法访问即可
 * @author bing  @create 2020/5/13 2:25 下午
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":"; //所以key拼接时用 ： 来分隔
    private static final String PREFIX_ENTITY_LIKE = "like:entity";//实体为key 赞前缀
    private static final String PREFIX_USER_LIKE = "like:user";//以用户为key 赞前缀


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
}





























