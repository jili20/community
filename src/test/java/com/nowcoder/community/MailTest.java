package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


/**
 * @author bing  @create 2020/5/7 8:37 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;
    @Autowired  // 把邮件信息传到模板，需要调用模板引擎
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail()
    {
        mailClient.sendMail("bing_yu2001@qq.com","Test","Welcome");
    }
    
    @Test
    public void testHtmlMail()
    {
        Context context = new Context();
        context.setVariable("username","sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("bing_yu2001@qq.com","HTML",content);
    }
}