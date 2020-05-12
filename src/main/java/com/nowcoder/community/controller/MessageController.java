package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/** 私信控制器
 * @author bing  @create 2020/5/12 11:50 上午
 */
@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET) // 方法的返回路径
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));//当前会话数量
        //会话列表
        List<Message> conversationsList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        //查询数量
        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationsList != null) {
            for (Message message : conversationsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message); // 下面是未读私信数量
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));//会话里包含几条消息
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //如果当前用户是消息发起者，目标就是接收人，否则是，否则目标是信息发起者
                int targetId = user.getId() == message.getFromId() ? message.getToId() :message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        //查询用户所有未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    // 私信详情页面
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        //分页信息
        page.setLimit(5);//每页显示5条
        page.setPath("/letter/detail/" + conversationId); // 当前路径
        page.setRows(messageService.findLetterCount(conversationId));//设置行数
        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String,Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);

            }
        }
        model.addAttribute("letters", letters);
        //查询私信目标，发送给模板显示
        model.addAttribute("target",getLetterTarget(conversationId));

        return "/site/letter-detail";
    }


    // 私信目标
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");//拆分成两个id
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }
}














