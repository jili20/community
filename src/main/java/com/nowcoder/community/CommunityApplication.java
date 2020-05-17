package com.nowcoder.community;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;

        import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

    @PostConstruct // 管理bean的生命周期 ;elasticsearch 相关的类:Netty4Utils;NettyRuntime
    public void init(){
        // 解决netty启动冲突问题;elasticsearch
        // 看 Netty4Utils.setAvailableProcessors()方法
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
