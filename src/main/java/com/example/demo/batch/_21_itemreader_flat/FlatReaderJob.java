package com.example.demo.batch._21_itemreader_flat;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
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
public class FlatReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    job->step->tasklet
//    job->step->chunk->reader->writer

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
                .resource(new ClassPathResource("user.txt"))
//                解析数据--指定解析器使用#分割 --默认是,号
                .delimited().delimiter("#")
//                按照 # 截取数据之后，数据怎么命名
                .names("id","name","age")
//                封装数据--将读取的数据封装到对象：User对象
                .targetType(User.class)

                .build();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step")
                .<User,User>chunk(1)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("flat-reader-job")
                .start(step())
                .build();
    }

    public static void main(String[] args){
        SpringApplication.run(FlatReaderJob.class,args);
    }

















}
