package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import java.util.List;
/** 业务层
 * @author bing  @create 2020/5/11 1:55 下午
 */
@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private  DiscussPostService discussPostService;
    @Autowired
    private SensitiveFilter sensitiveFilter;//过滤敏感词

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    // 添加评论；使用声明式事务；隔离级别、传播机制
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//转义HTML标记
        comment.setContent(sensitiveFilter.filter(comment.getContent()));//过滤敏感词
        int rows = commentMapper.insertComment(comment);
        //更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }
        return rows;
    }
}





















