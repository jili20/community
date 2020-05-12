//package com.nowcoder.community.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
///** AOP 方面组件，统一处理日志
// * @author bing  @create 2020/5/12 11:01 下午
// */
////@Component
////@Aspect
//public class AlphaAspect {
//    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))") // service 包下的所有方法所有参数所有返回值
//    public void pointcut(){ // 切点
//
//    }
//
//    @Before("pointcut()")
//    public void before(){ // 在前面记日志
//        System.out.println("before");
//    }
//
//    @After("pointcut()")
//    public void after(){ // 在后面记日志
//        System.out.println("after");
//    }
//
//    @AfterReturning("pointcut()")
//    public void afterReturning(){ // 有了返回值以后记日志
//        System.out.println("afterReturning");
//    }
//
//    @AfterThrowing("pointcut()")
//    public void afterThrowing(){ // 在势出异常的时候记日志
//        System.out.println("afterThrowing");
//    }
//
//    @Around("pointcut()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{ // 环绕，即在前面记录日志，也在后面记录日志
//        System.out.println("around.before");
//        Object obj = joinPoint.proceed();//调用目标组件的方法
//        System.out.println("around.after");
//        return obj;
//    }
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
