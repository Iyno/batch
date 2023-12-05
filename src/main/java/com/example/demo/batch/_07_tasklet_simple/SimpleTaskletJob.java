package com.example.demo.batch._07_tasklet_simple;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


//开启spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
@SpringBootApplication
public class SimpleTaskletJob {
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

    @Bean
    public Tasklet tasklet77(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
//

                System.out.println("-------------1-----------");
//                作业

                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }






    //	构造一个step对象
    @Bean
    public Step step77(){
        return stepBuilderFactory.get("step77").tasklet(tasklet77())
                .build();
    }


    @Bean
    public Job job77(){
         return jobBuilderFactory.get("tasklet-simple-job")
                 .start(step77())
                 .incrementer(new RunIdIncrementer())//run.id自动增长
                 .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(SimpleTaskletJob.class, args);
    }

}

