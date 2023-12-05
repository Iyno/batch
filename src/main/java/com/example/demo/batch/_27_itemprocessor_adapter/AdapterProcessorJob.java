package com.example.demo.batch._27_itemprocessor_adapter;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
public class AdapterProcessorJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }


    @Bean
    public FlatFileItemReader<User> itemReader(){
        return new FlatFileItemReaderBuilder<User>()
//                设置文件-读文件
                .name("userItemReader")
                .resource(new ClassPathResource("users-adapter.txt"))
//                解析数据--指定解析器使用#分割 --默认是,号
                .delimited().delimiter("#")
//                按照 # 截取数据之后，数据怎么命名
                .names("id","name","age")
//                封装数据--将读取的数据封装到对象：User对象
                .targetType(User.class)

                .build();
    }

//    已经定义好的 用户名转换类
//    当前需求：使用适配器处理器调用该类对象UserServiceImpl的toUppeCase()方法
//    实现用户名转换成大写
    @Bean
    public UserServiceImpl userService(){
        return new UserServiceImpl();
    }

    @Bean
    public ItemProcessorAdapter<User,User> itemProcessorAdapter(){
        ItemProcessorAdapter<User, User> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetMethod("toUppeCase");//将要调用的适配器指定的方法
        adapter.setTargetObject(userService());//找到适配器逻辑
        return adapter;
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step")
                .<User, User>chunk(1)
                .reader(itemReader())
                .processor(itemProcessorAdapter())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("adapter-process-job")
                .start(step())
                .build();
    }

    public static void main(String[] args){
        SpringApplication.run(AdapterProcessorJob.class,args);
    }

















}
