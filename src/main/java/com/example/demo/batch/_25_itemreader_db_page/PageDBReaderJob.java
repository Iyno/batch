package com.example.demo.batch._25_itemreader_db_page;





import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableBatchProcessing
@SpringBootApplication
public class PageDBReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;


    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }

//    将列数据与对象属性一一映射
    @Bean
    public UserRowMapper userRowMapper(){
        return new UserRowMapper();
    }
    
    @Bean
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("select *");//查询语句
        factoryBean.setFromClause("from user");//查询表
        factoryBean.setWhereClause("where age > :age");//:age表示占位符
        factoryBean.setSortKey("id");
        return factoryBean.getObject();

    }

    /**
     * 使用jdbc游标方式读数据
     * select * from user where ...limit ？，pageSize
     * 
     * @return
     */
    @Bean
    public JdbcPagingItemReader<User> itemReader() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("age",16);
            return new JdbcPagingItemReaderBuilder<User>()
                    .name("userItemReader")
                    .dataSource(dataSource)
                    .rowMapper(userRowMapper())
                    .queryProvider(pagingQueryProvider())//分页逻辑
                    .parameterValues(map)//sql条件
                    .pageSize(10)
                    .build();


    }


    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)
                .reader(itemReader())
                .writer(itemWriter())
                .build();

    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("cursor-db-reader-job")
                .start(step())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args){
        SpringApplication.run(PageDBReaderJob.class, args);
    }




}
