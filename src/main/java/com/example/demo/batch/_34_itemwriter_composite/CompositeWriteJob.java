package com.example.demo.batch._34_itemwriter_composite;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

import javax.sql.DataSource;
import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
public class CompositeWriteJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;

//输出到控制台

    @Bean
    public ItemWriter<User> cosoleItemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }


//    输出到outUser.txt文件
    @Bean
    public FlatFileItemWriter<User> flatFileItemWriter(){
        return new FlatFileItemWriterBuilder<User>()
                .name("userFlatItemWriter")
//                输出位置
                .resource(new PathResource("/Users/aifanghuang/Downloads/test/outUser.txt"))
                .formatted()//要进行格式输出
                .format("id: %s,姓名: %s,年龄: %s")//输出数据格式
                .names("id","name","age")//需要输出数据属性
                .shouldDeleteIfEmpty(true)//如果读入数据为空，输出时穿件文件直接删除
                .shouldDeleteIfExists(true)//如果输出文件已经存在，则删除
                .append(true)//如果文件已经存在，不删除，直接追加到现有文件
                .build();

    }


//    json对象的调度器
    @Bean
    public JacksonJsonObjectMarshaller jsonObjectMarshaller(){
        return new JacksonJsonObjectMarshaller<>();
    }
//json格式的输出
    @Bean
    public JsonFileItemWriter<User> jsonFileItemWriter(){
        return new JsonFileItemWriterBuilder<User>()
                .name("jsonUserItemWriter")
                .resource(new PathResource("/Users/aifanghuang/Downloads/test/jsonUser.json"))
//                json对象调度器--将user对象缓存json格式，输出文件
                .jsonObjectMarshaller(jsonObjectMarshaller())
                .build();

    }

    @Bean
    public UserPreStatementSetter userPreStatementSetter(){
        return new UserPreStatementSetter();
    }

//数据库输出
    @Bean
    public JdbcBatchItemWriter<User> jdbcBatchItemWriter(){
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("insert into user(id,name,age) values(?,?,?)")
//                设置sql中的占位符参数
                .itemPreparedStatementSetter(userPreStatementSetter())
                .build();
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
    public CompositeItemWriter<User> compositeItemWriter(){
        return new CompositeItemWriterBuilder<User>()
                .delegates(cosoleItemWriter(),flatFileItemWriter(),jsonFileItemWriter(),jdbcBatchItemWriter())
                .build();
    }
    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)
                .reader(itemReader())
                .writer(compositeItemWriter())
                .build();
    }

    @Bean
    public Job job1(){
        return jobBuilderFactory.get("composite-writer-job")
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args){
        SpringApplication.run(CompositeWriteJob.class,args);
    }

















}
