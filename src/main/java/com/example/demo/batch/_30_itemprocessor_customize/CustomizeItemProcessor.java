package com.example.demo.batch._30_itemprocessor_customize;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

//自定义解析器
public class CustomizeItemProcessor implements ItemProcessor<User,User> {

    @Override
    public User process(User item) throws Exception {
//        将id为偶数数据获取，其他放弃--返回null
        return item.getId() % 2 == 0 ? item: null;
    }
}
