package com.nowcoder.community;

import com.nowcoder.community.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author bing  @create 2020/5/11 12:51 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTest {
    
    @Autowired
    private TestService testService;
    
    @Test
    public void testSavel()
    {
        Object obj = testService.savel();
        System.out.println(obj);
    }

    @Test
    public void testSavel2()
    {
        Object obj = testService.save2();
        System.out.println(obj);
    }
}
