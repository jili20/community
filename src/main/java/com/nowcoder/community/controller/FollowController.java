package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author bing  @create 2020/5/14 10:39 上午
 */
@Controller
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;//获取当前登录用户

    // 关注
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody //因为是异步，加这个注解
    public String follow(int entityType, int entityId){ // 自己加拦截器，登录可操作此功能
        User user = hostHolder.getUser(); // 获取当前登录用户
        followService.follow(user.getId(), entityType,entityId);//关注
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
}





















