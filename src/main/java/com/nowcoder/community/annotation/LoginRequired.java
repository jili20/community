package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 自定义注解：程序需要登录以后才能访问
 * @author bing  @create 2020/5/10 1:22 下午
 */
@Target(ElementType.METHOD) //作用在方法上
@Retention(RetentionPolicy.RUNTIME) //程序运行的时候有效
public @interface LoginRequired {
}


































