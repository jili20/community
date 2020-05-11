package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * @author bing  @create 2020/5/6 11:27 下午
 */
@Repository
@Mapper
public interface DiscussPostMapper {

    // offset 起始行号（页数）？，每页显示多少条数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param 注解用于给参数取别名
    // 如果只有一个参数，并且在 <if> 里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
    //添加帖子
    int insertDiscussPost(DiscussPost discussPost);
    //帖子详情
    DiscussPost selectDiscussPostById(int id);
    //更新帖子的数量
    int updateCommentCount(int id, int commentCount);
}


















