package com.example.demo.batch._01_helloword;




import org.springframework.batch.core.Job;
        import org.springframework.batch.core.Step;
        import org.springframework.batch.core.StepContribution;
        import org.springframework.batch.core.configuration.JobLocator;
        import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
        import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
        import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
        import org.springframework.batch.core.job.builder.JobBuilder;
        import org.springframework.batch.core.launch.JobLauncher;
        import org.springframework.batch.core.scope.context.ChunkContext;
        import org.springframework.batch.core.step.builder.StepBuilder;
        import org.springframework.batch.core.step.tasklet.Tasklet;
        import org.springframework.batch.repeat.RepeatStatus;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.context.annotation.Bean;


//开启spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
@SpringBootApplication
public class HelloJob {
    //	作业启动器
    @Autowired
    private JobLauncher jobLauncher;
    //	job构造工厂
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //构造一个step对象执行的任务（逻辑对象）
    @Bean
    public Tasklet tasklet2(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
//				要执行逻辑--step步骤执行逻辑
                System.out.println("hello spring batch!");
                return RepeatStatus.FINISHED;//执行完成
            }
        };
    }




    //	构造一个step对象
    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2").tasklet(tasklet2())
                .build();
    }

    @Bean
    public Job job2(){
        return jobBuilderFactory.get("hello-job").start(step2()).build();
    }

//    public static void main(String[] args) {
//        SpringApplication.run(com.example.demo.DemoApplication.class, args);
//    }

}
