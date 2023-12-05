package com.example.demo.batch._22_itemreader_flat_mapper;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
public class MapperFlatReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public UserFieldMapper userFieldMapper(){
        return new UserFieldMapper();
    }


    @Bean
    public FlatFileItemReader<User> userItemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userMapperItemReader")
                .resource(new ClassPathResource("users2.txt"))
                .delimited().delimiter("#")
                .names("id", "name", "age", "province", "city", "area")
                .fieldSetMapper(userFieldMapper())
                .build();
    }

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
    public Step step(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)
                .reader(userItemReader())
                .writer(itemWriter())
                .build();

    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("mapper1-flat-reader-job")
                .start(step())
                .build();
    }

    public static void main(String[] args){
        SpringApplication.run(MapperFlatReaderJob.class, args);
    }




}
