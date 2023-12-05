package com.example.demo.batch._02_params;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.Objects;


//开启spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
@SpringBootApplication
public class ParamJob {
    //	作业启动器
    @Autowired
    private JobLauncher jobLauncher;
    //	job构造工厂
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //构造一个step对象执行的任务（逻辑对象）
//    
    @StepScope
    @Bean
    public Tasklet tasklet(@Value("#{jobParameters['name']}")String name){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
//                方案1：使用chunkContext
//                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//                System.out.println("params--name:" + jobParameters.get("name"));
//                方案2：使用@Value
                System.out.println("params--name:" + name);
//				要执行逻辑--step步骤执行逻辑
                System.out.println("param job spring batch!");
                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }




    //	构造一个step对象
    @Bean
    public Step step(){
        return stepBuilderFactory.get("step").tasklet(tasklet(null))
                .build();
    }

  /**n
  **ic Job job(){
  **return jobBuilderFactory.get("param-chunk-job").start(step()).build();
  **/
    @Bean
    public Job job(){
         return jobBuilderFactory.get("param1-value-job").start(step()).build();
    }


    public static void main(String[] args) {
        SpringApplication.run(ParamJob.class, args);
    }

}

