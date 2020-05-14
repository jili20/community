package com.nowcoder.community.controller;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
/**
 * @author bing  @create 2020/5/10 10:40 上午
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);// 日志

    @Value("${community.path.upload}")
    private String uploadPath; // 上传路径

    @Value("${community.path.domain}")
    private String domain; // 域名路径

    @Value("${server.servlet.context-path}")
    private String contextPath; // 项目路径

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder; // 容器，用于代替session对象

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired // 使用自定义注解，登录以后才能访问
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    // 处理上传头像
    @LoginRequired // 使用自定义注解，登录以后才能访问
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }
        //获取用户上传的原文件名，截取后缀，判断是否正确
        String fileName = headerImage.getOriginalFilename();//获取用户上传原始文件名
        String suffix = fileName.substring(fileName.lastIndexOf("."));// 最后一个.的索引往后截取
        if (StringUtils.isBlank(suffix)) {  // 如果文件的后缀是空的
            model.addAttribute("error", "文件的格式不正确！");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest); // 存储文件
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }
        // 更新当前用户的头像的路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    // 获取头像
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 服务器存入路径
        fileName = uploadPath + "/" + fileName;
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try(
                FileInputStream fis = new FileInputStream(fileName); //读取文件输入流
                OutputStream os = response.getOutputStream(); // 得到输出流

        ) {
            byte[] buffer = new byte[1024];// 声明一个缓冲区，一次性输出用
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    // 个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = (int) likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}





























