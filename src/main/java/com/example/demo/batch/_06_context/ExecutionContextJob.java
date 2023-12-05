package com.example.demo.batch._06_context;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.demo.batch._04_param_incr.DailyTimestampParamIncrementer;
import com.example.demo.batch._05_job_listerner.JobStateListener;
import com.example.demo.batch._05_job_listerner.StateListenerAnoJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
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

import java.util.Map;


//开启spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
@SpringBootApplication
public class ExecutionContextJob {
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
//                步骤
//                可以获取共享数据，但是不允许修改
//                Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
//                通过执行上下文对象获取跟设置擦身
                ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                stepEC.put("key-step1-step","value-step1-step");

                System.out.println("-------------1-----------");
//                作业
                ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                jobEC.put("key-step1-job","value-step1-job");
                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }
    @Bean
    public Tasklet tasklet772(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {

//步骤
                ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                System.err.println(stepEC.get("key-step1-step"));

                System.out.println("-------------2-----------");
//                作业
                ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                System.err.println(jobEC.get("key-step1-job"));
//
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
    public Step step772(){
        return stepBuilderFactory.get("step772").tasklet(tasklet772())
                .build();
    }

    @Bean
    public Job job77(){
         return jobBuilderFactory.get("api-execution-context-job")
                 .start(step77())
                 .next(step772())
                 .incrementer(new RunIdIncrementer())//run.id自动增长
                 .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(ExecutionContextJob.class, args);
    }

}

