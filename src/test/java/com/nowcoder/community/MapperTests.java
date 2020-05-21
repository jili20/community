package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author bing  @create 2020/5/6 9:06 下午
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;


    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser()
    {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser()
    {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);

    }

    // 帖子表
    @Test
    public void testSelectPosts()
    {
//        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);  // 查询所有帖子，10 条为一页
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10,0); // 查询用户id 149 所有帖子
        for (DiscussPost post : list) {
            System.out.println(post);
        }

//        int rows = discussPostMapper.selectDiscussPostRows(0); // 查询所有帖子总数
        int rows = discussPostMapper.selectDiscussPostRows(149); // 查询用户id 149 的帖子总数
        System.out.println(rows);
    }

    // 测试 login_ticket 表
    @Test
    public void testInsertLoginTicket()
    {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }


    @Test
    public void testSelectConversations()
    {
        //查询userid 111 用户的私信
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for (Message message : list) {
            System.out.println(message);
        }

        //2、查询当前用户的会话数量
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        //3、查询某个会话所包含的私信列表，私信详情页面使用
        list = messageMapper.selectLetters("111_112", 0,10);
        for (Message message : list) {
            System.out.println(message);
        }

        //4、查询某个会话所包含的私信数量
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        //5、查询未读私信的数量
        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);

    }

}























