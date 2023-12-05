package com.example.demo.batch1.batch._05_job_listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobStateListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.err.println("执行前："+ jobExecution.getStatus());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.err.println("执行后："+ jobExecution.getStatus());

    }
}
