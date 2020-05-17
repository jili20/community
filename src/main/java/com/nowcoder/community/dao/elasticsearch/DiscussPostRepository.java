package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
/**
 * @author bing  @create 2020/5/17 3:40 下午
 */
@Repository //针对数据库访问的注解
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
