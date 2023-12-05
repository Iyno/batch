package com.example.demo.batch1.batch._05_job_listener;

import com.example.demo.batch1.batch._04_param_incr.DailyTimestampParamIncrementer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
@EnableBatchProcessing
public class StatusListenerJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
                System.err.println("执行中:" + jobExecution.getStatus());
                return RepeatStatus.FINISHED;
            }
        };
    }


    //时间戳
    @Bean
    public DailyTimestampParamIncrementer dailyTimestampParamIncrementer(){
        return new DailyTimestampParamIncrementer();
    }


//    状态监听器
    @Bean
    public JobStateListener jobStateListener(){
       return new JobStateListener();
    }

    @Bean
    public Step step1(){
        return  stepBuilderFactory.get("step1")
                .tasklet(tasklet())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("state1--listener--job")
                .start(step1())
//                .incrementer(dailyTimestampParamIncrementer())
                .listener(jobStateListener())
                .incrementer(new RunIdIncrementer())
                //.incrementer(new RunIdIncrementer())  //参数增量器(run.id自增)
//                .incrementer(dailyTimestampParamIncrementer())  //时间戳增量器
                .build();
    }
    public static void main(String[] args) {
        SpringApplication.run(StatusListenerJob.class, args);
    }
}
