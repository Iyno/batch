package com.example.demo.batch._03_param_validator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


//开启spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
@SpringBootApplication
public class ParamValidatorJob {
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
    public Tasklet tasklet4(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                    throws Exception {
//                方案1：使用chunkContext
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                System.out.println("params-必填-name:" + jobParameters.get("name"));
                System.out.println("params-可选-age:"  + jobParameters.get("age"));
                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }




    //	构造一个step对象
    @Bean
    public Step step4(){
        return stepBuilderFactory.get("step").tasklet(tasklet4())
                .build();
    }


//    自定义参数校验器
    @Bean
    public NameParamValidator nameParamValidator(){
        return new NameParamValidator();
    }

    @Bean
    public DefaultJobParametersValidator defaultJobParametersValidator(){
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        //必传参数
        validator.setRequiredKeys(new String[]{"name"});
//        可选参数
        validator.setOptionalKeys(new String[]{"age"});
        return validator;
    }


    //组合参数解析器

    @Bean
    public CompositeJobParametersValidator compositeJobParametersValidator() throws Exception {
        CompositeJobParametersValidator  validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(nameParamValidator(),defaultJobParametersValidator()));
        validator.afterPropertiesSet();
        return validator;
    }

//    @Bean
//    public Job job3(){
//        return jobBuilderFactory.get("default-name-age-param-validate-job")
//                .start(step3())
//                .validator(defaultJobParametersValidator())//指定参数的校验器
//                .build();
//    }

    @Bean
    public Job job4() throws Exception {
        return jobBuilderFactory.get("composite-name-age-param-validate-job")
                .start(step4())
                .validator(compositeJobParametersValidator())//指定参数的校验器
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(com.example.demo.DemoApplication.class, args);
    }

}

