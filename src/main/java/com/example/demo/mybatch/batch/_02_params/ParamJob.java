package com.example.demo.mybatch.batch._02_params;

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

import java.util.Map;
import java.util.Objects;

@EnableBatchProcessing
@SpringBootApplication
public class ParamJob {
    //job调度器
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //任务-step执行逻辑由tasklet完成
    @Bean
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();

                System.out.println("params--name:" + parameters.get("name"));
                return RepeatStatus.FINISHED;
            }
        };
    }

    //作业步骤-不带读/写/处理
    @Bean
    public Step step(){
        return stepBuilderFactory.get("step")
                .tasklet(tasklet())
                .build();
    }
    @Bean
    public Job job(){
        return jobBuilderFactory.get("param -job")
                .start(step())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static void main(String[] args) {
        SpringApplication.run(ParamJob.class, args);
    }

}
