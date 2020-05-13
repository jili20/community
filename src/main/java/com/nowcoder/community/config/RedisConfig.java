package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
/** 配置redis
 * @author bing  @create 2020/5/13 12:16 下午
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){ //用它访问数据，要先创建连接,连接工厂
        RedisTemplate<String, Object> template = new RedisTemplate<>();//实例化
        template.setConnectionFactory(factory);//把工厂设置给template，之后具备了访问数据库的能力

        //设置key的序列化方式，序列化字符串
        template.setKeySerializer(RedisSerializer.string());
        //设置普通的value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();//触发，使生效
        return template;
    }
}



















