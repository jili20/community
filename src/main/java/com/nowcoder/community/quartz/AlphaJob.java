package com.nowcoder.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/**
 * @author bing  @create 2020/5/19 9:16 下午
 */
public class AlphaJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 打印当前线程和一句话
        System.out.println(Thread.currentThread().getName() + ": execute a quartz job.");
    }
}
