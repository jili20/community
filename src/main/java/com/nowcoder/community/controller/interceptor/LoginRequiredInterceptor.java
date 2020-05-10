package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author bing  @create 2020/5/10 1:32 下午
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) { // 如果是这个类型
            HandlerMethod handlerMethod = (HandlerMethod) handler; // 转换类型，不用 object
            Method method = handlerMethod.getMethod(); // 获取到方法对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class); // 通过方法对象获取指定类型的注解
            if (loginRequired != null && hostHolder.getUser() == null) { // 当前方法需要登录，但用户没登录；不等于空，意味方法需要登录才能访问；
                response.sendRedirect(request.getContextPath()+"/login"); // 重写向到 项目路径 下的 login 登录页面
                return false;
            }
        }
        return true;
    }
}


































