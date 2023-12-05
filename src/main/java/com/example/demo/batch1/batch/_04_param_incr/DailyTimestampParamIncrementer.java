package com.example.demo.batch1.batch._04_param_incr;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

public class DailyTimestampParamIncrementer implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters jobParameters) {
        return new JobParametersBuilder(jobParameters)
                .addLong("daily",new Date().getTime())//添加时间戳
                .toJobParameters();
    }
}
