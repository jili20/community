package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
/** AOP 统一处理记录日志
 * @author bing  @create 2020/5/12 11:31 下午
 */
//@Component
//@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class); // 实例化日志
    // 声明切点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))") // service 包下的所有方法所有参数所有返回值
    public void pointcut(){ // 切点

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){ // 在前面记日志
        // 用户【1.2.3.4】，在[xxx],访问了【com.nowcoder.community.service.xxx()].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();//转子类型
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();//得到目标类型名
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));

    }

}































