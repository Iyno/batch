package com.example.demo.batch._23_itemreader_flat_json;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
public class JsonFlatReaderJob {
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
    public JsonItemReader<User> itemReader(){
//        当前读取json使用的是阿里的jackson 框架
//        参数：读取json格式文件转换成具体类型：User.class
        JacksonJsonObjectReader<User> jsonObjectReader = new JacksonJsonObjectReader<>(User.class);
        ObjectMapper objectMapper = new ObjectMapper();
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<User>()
                .name("userItemReaer")
                .resource(new ClassPathResource("user.json"))
                .jsonObjectReader(jsonObjectReader)
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)
                .reader(itemReader())
                .writer(itemWriter())
                .build();

    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("json1-flat-reader-job")
                .start(step())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args){
        SpringApplication.run(JsonFlatReaderJob.class, args);
    }




}
