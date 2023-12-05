package com.example.demo.batch._37_step_part;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//用户文件读取分区器
//作用：指定从步骤的名称 + 配置从步骤需要上下文环境
public class UserPartitioner implements Partitioner {
//    map:  key:从步骤名称 value:从步骤上下文环境
//    gridSize:从步骤个数
//    逻辑：
//    定义从步骤1--关联上下文环境--指定要处理文件名：file :user1-10.txt
    //    定义从步骤2--关联上下文环境--指定要处理文件名：file :user11-20.txt

    int begin = 1;
    int end = 10;
    int range = 10;
    String text = "user%s-%s.txt";//名字规则
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>(gridSize);
        for (int i = 0; i<gridSize;i++){
            ExecutionContext value = new ExecutionContext();
            String name = String.format(text,begin,end);
            Resource resource = new ClassPathResource(String.format(text,begin,end));
//            springbatch转换成，单纯使用对象时会报错，可以使用url字符串形式，
//            spring会自动加载
            try {
                value.putString("file",resource.getURL().toExternalForm());
            } catch (IOException e) {
                e.printStackTrace();
            }

            begin += range;
            end += range;
            result.put("user_partition_" + i,value);
        }

        return result;
    }
}
