package com.example.demo.batch._11_step_condition_decider;

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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


//开启spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
@SpringBootApplication
public class CustomizeStatusStepJob {
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
    public Tasklet firstTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
                System.out.println("-------------firstTasklet-----------");
                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }
    @Bean
    public Tasklet TaskletA(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
                System.out.println("-------------TaskletA-----------");

                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }
    @Bean
    public Tasklet TaskletB(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
                System.out.println("-------------TaskletB-----------");
                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }
    @Bean
    public Tasklet taskletDefault(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
                System.out.println("-------------taskletDefault-----------");
                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }
    //	构造一个step对象
    @Bean
    public Step firstStep(){
        return stepBuilderFactory.get("firstStep").tasklet(firstTasklet())
                .build();
    }

    @Bean
    public Step stepA(){
        return stepBuilderFactory.get("stepA").tasklet(TaskletA())
                .build();
    }

    @Bean
    public Step stepB(){
        return stepBuilderFactory.get("stepB").tasklet(TaskletB())
                .build();
    }

    @Bean
    public Step stepDefault(){
        return stepBuilderFactory.get("stepDefault").tasklet(taskletDefault())
                .build();
    }

    @Bean
    public MyStatusDecider myStatusDecider(){
        return new MyStatusDecider();

    }

    @Bean
    public Job job(){

         return jobBuilderFactory.get("tasklet-simple-job")
                 .start(firstStep())
                 .next(myStatusDecider())
                 .from(myStatusDecider()).on("A").to(stepA())
                 .from(myStatusDecider()).on("B").to(stepB())
                 .from(myStatusDecider()).on("*").to(stepDefault())
                 .end()
                 .incrementer(new RunIdIncrementer())//run.id自动增长
                 .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(CustomizeStatusStepJob.class, args);
    }

}

