package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
/** 评论dao
 * @author bing  @create 2020/5/11 1:36 下午
 */
@Repository
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit); // 查评论

    int selectCountByEntity(int entityType, int entityId); // 查评论总数

    // 添加评论
    int insertComment(Comment comment);
}




























