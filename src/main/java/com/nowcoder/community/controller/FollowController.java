package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
/**
 * @author bing  @create 2020/5/14 10:39 上午
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;//获取当前登录用户

    @Autowired
    private UserService userService;

    @Autowired // kafka - 消息通知 - 生产者
    private EventProducer eventProducer;

    // 关注
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody //因为是异步，加这个注解
    public String follow(int entityType, int entityId){ // 自己加拦截器，登录可操作此功能
        User user = hostHolder.getUser(); // 获取当前登录用户
        followService.follow(user.getId(), entityType,entityId);//关注

        // 点关注触发消息通知事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,"已关注");
    }

    // 取消关注
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody //因为是异步，加这个注解
    public String unfollow(int entityType, int entityId){ // 自己加拦截器，登录可操作此功能
        User user = hostHolder.getUser(); // 获取当前登录用户
        followService.unfollow(user.getId(), entityType,entityId);//取消关注
        return CommunityUtil.getJSONString(0,"已取消关注");
    }

    // 查询某个用户关注的人
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        // 设置分页
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));//强转类型
        // 分页查询数据
        List<Map<String,Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String,Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));//hasFollowed 调用下面封装的访求
            }
        }
        model.addAttribute("users",userList);
        return "/site/followee";
    }


    // 查询某个用户的粉丝
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        // 设置分页
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));//强转类型
        // 分页查询数据
        List<Map<String,Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String,Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));//hasFollowed 调用下面封装的访求
            }
        }
        model.addAttribute("users",userList);
        return "/site/follower";
    }

    // 封装方法，方便上面调用
    private boolean hasFollowed(int userId){
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }

}





















