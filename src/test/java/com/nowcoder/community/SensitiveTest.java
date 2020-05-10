package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/** 测试过滤敏感词工具类
 * @author bing  @create 2020/5/10 5:48 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter()
    {
        String text = "这里可以赌博，可以嫖娼，可以吸毒，可以开票,哈哈哈！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以赌&博，可以嫖&娼，可以吸-毒，可以开@票,哈哈哈！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

    }
}
















