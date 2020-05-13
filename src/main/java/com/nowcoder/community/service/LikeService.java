package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
/** 点赞业务层
 * @author bing  @create 2020/5/13 2:47 下午
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞:用户id、哪个实体、实体id
    public void like(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);//生成 redis key的工具参数
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);//判断userId在不在集合里
        if (isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey,userId);//取消赞
        }else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);//点赞
        }
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);//生成 redis key
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态（是否点赞）
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);//生成 redis key
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0; // 1 点赞；0 没点赞；
    }
}



























