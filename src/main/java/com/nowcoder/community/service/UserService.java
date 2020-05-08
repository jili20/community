package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author bing  @create 2020/5/7 11:21 上午
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient; // 注入邮件功能
    @Autowired
    private TemplateEngine templateEngine; // 注入模板引擎
    @Value("${community.path.domain}")
    private String domain; // 加入域名
    @Value("${server.servlet.context-path}")
    private String contextPath; // 加入项目路径


    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        // 处理空值
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }
        // 验证用户名
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg","该用户名已存在！");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByName(user.getEmail());
        if (u != null) {
            map.put("emailMsg","该邮箱已被注册！");
            return map;
        }
        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // 发送激活的 地址： 域名/项目名/activation/用户id/激活码
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活码",content);

        return map;
    }
}













