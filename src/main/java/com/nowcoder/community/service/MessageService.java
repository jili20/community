package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author bing  @create 2020/5/12 11:37 上午
 */
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    //1、查询当前用户的会话列表，会对每个会话只返回一条最新的私信
    public List<Message> findConversations(int userId,int offset, int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }

    //2、查询当前用户的会话数量
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    //3、查询某个会话所包含的私信列表，私信详情页面使用
    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    //4、查询某个会话所包含的私信数量
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    //5、查询未读私信的数量
    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }
}






































