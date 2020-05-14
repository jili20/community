package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
/**
 * @author bing  @create 2020/5/14 10:22 上午
 */
@Service
public class FollowService {

    @Autowired //因为要把数据存到redis，所以把它注入进来
    private RedisTemplate redisTemplate;

    // 关注
    public void follow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);//关注的实体
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);//实体的粉丝
                operations.multi();//启用事务
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());// 有序集合:key;关注的实体id:时间
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());// 有序集合:key;粉丝id:时间
                return operations.exec();
            }
        });
    }

    // 取消关注
    public void unfollow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);//关注的实体
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);//实体的粉丝
                operations.multi();//启用事务
                operations.opsForZSet().remove(followeeKey,entityId);// 有序集合:key;关注的实体id
                operations.opsForZSet().remove(followerKey,userId);// 有序集合:key;粉丝id
                return operations.exec();
            }
        });
    }

    // 查询关注的实体的数量
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询实体的粉丝的数量
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
}


























