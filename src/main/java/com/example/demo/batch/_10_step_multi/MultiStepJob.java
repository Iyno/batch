package com.example.demo.batch._10_step_multi;

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
public class MultiStepJob {
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
                throw new RuntimeException("假装失败了");
//                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }

    @Bean
    public Tasklet successTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
                System.out.println("-------------successTasklet-----------");

                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }
    @Bean
    public Tasklet failedTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
                System.out.println("-------------failedTasklet-----------");
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
    public Step successStep(){
        return stepBuilderFactory.get("successStep").tasklet(successTasklet())
                .build();
    }

    @Bean
    public Step failedStep(){
        return stepBuilderFactory.get("failedStep").tasklet(failedTasklet())
                .build();
    }

//    如果firstStep执行成功，下一步执行successStep 否则是failedStep
    @Bean
    public Job job(){
/**
 * 假设
 *fisrtStep()返回状态变量是：0
 * if(a.equals("FAILED"))
 *    .to(failStep())
 * else
 *     .to(successStep())
 *
 */
         return jobBuilderFactory.get("tasklet-simple-job")
                 .start(firstStep()).on("FAILED").to(failedStep())//满足xx条件，执行后面的逻辑：满足firstStep执行返回状态为失败状态，failed执行failedstep
                 .from(firstStep()).on("*").to(successStep())
                 .end()
                 .incrementer(new RunIdIncrementer())//run.id自动增长
                 .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(MultiStepJob.class, args);
    }

}

