package com.example.demo.batch._09_step_listener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;


//开启spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
@SpringBootApplication
public class StepListenerJob {
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
    int timer = 10;
//    读操作
    @Bean
    public ItemReader<String> itemReader(){
        return new ItemReader<String>() {
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if(timer>0){
                    System.out.println("-------------read-----------");
                    return "read-ret" + timer--;

                }else {
                    return null;
                }

            }
        };
    }

//    处理操作
    @Bean
    public ItemProcessor<String ,String> itemProcessor(){
        return new ItemProcessor<String,String>() {
            @Override
            public String process(String item) throws Exception {
                System.out.println("-------------process-----------");
                return "process-ret->" + item;

            }
        };
    }

//    写操作
    @Bean
    public ItemWriter<String> itemWriter(){
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                System.out.println(items);
            }
        };
    }

    @Bean
    public MyStepListener myStepListener(){
        return new MyStepListener();
    }

    //	构造一个step对象
    @Bean
    public Step step77(){
        return stepBuilderFactory.get("step77")
                .<String,String>chunk(3)//暂时为3
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .listener(myStepListener())
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
        SpringApplication.run(StepListenerJob.class, args);
    }

}

