package com.nowcoder.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.File;
/**
 * @author bing  @create 2020/5/21 10:31 上午
 */
@Configuration
public class WKConfig {

    private static final Logger logger = LoggerFactory.getLogger(WKConfig.class);

    @Value("${wk.image.storage}")
    private String WkImageStore;

    @PostConstruct
    public void init(){
        //创建Wk图片目录
        File file = new File(WkImageStore);
        if (!file.exists()){
            file.mkdir();
            logger.info("创建WK图片目录：" + WkImageStore);
        }
    }
}
