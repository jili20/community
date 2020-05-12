package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * @author bing  @create 2020/5/11 10:11 下午
 */
@Repository
@Mapper
public interface MessageMapper {

    //1、查询当前用户的会话列表，会对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    //2、查询当前用户的会话数量
    int selectConversationCount(int userId);

    //3、查询某个会话所包含的私信列表，私信详情页面使用
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //4、查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //5、查询未读私信的数量
    int selectLetterUnreadCount(int userId, String conversationId);

    //6、新增消息
    int insertMessage(Message message);

    //7、修改消息的状态
    int updateStatus(List<Integer> ids, int status);

}
























