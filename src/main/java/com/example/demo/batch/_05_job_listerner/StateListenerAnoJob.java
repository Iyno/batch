package com.example.demo.batch._05_job_listerner;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

/**
 * 作业状态监听器--使用注解实现
 */
public class StateListenerAnoJob {
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.err.println("作业执行前的状态：" + jobExecution.getStatus());

    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.err.println("作业执行后的状态：" + jobExecution.getStatus());


    }
}
