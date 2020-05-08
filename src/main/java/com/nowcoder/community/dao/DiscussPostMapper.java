package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author bing  @create 2020/5/6 11:27 下午
 */
@Mapper
public interface DiscussPostMapper {

    // 起始行号，1页显示多少条数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param 注解用于给参数取别名
    // 如果只有一个参数，并且在 <if> 里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

}



















