package com.example.studyroom.batch;

import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.statistics.*;
import com.example.studyroom.repository.EnterHistoryRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.repository.statistics.*;


import com.example.studyroom.service.ShopService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ShopOccupancyBatchConfig {


    private final ShopService shopService;
    public ShopOccupancyBatchConfig(
    ShopService shopService) {

        this.shopService = shopService;
    }
    ////////////////////
    //시간당 이용자수 통계
    @Bean
    public Job shopUsageHourlyJob(JobRepository jobRepository, Step shopUsageHourlyStep) {
        return new JobBuilder("shopUsageHourlyJob", jobRepository)
                .start(shopUsageHourlyStep)
                .build();
    }

    @Bean
    public Step shopUsageHourlyStep(JobRepository jobRepository, Tasklet shopUsageHourlyTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("shopUsageHourlyStep", jobRepository)
                .tasklet(shopUsageHourlyTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet shopUsageHourlyTasklet() {
        return (contribution, chunkContext) -> {
            shopService.calculateAndSaveshopUsageHourly();
            return RepeatStatus.FINISHED;
        };
    }


    //////////////////////////////
    //하루 이용자수 통계
    @Bean
    public Job shopUsageDailyJob(JobRepository jobRepository, Step shopUsageDailyStep) {
        return new JobBuilder("shopUsageDailyJob", jobRepository)
                .start(shopUsageDailyStep)
                .build();
    }

    @Bean
    public Step shopUsageDailyStep(JobRepository jobRepository, Tasklet shopUsageDailyTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("shopUsageDailyStep", jobRepository)
                .tasklet(shopUsageDailyTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet shopUsageDailyTasklet() {
        return (contribution, chunkContext) -> {
            shopService.calculateAndSaveDailyOccupancy();
            return RepeatStatus.FINISHED;
        };
    }



    /////////////
    //시간대별 좌석id별 이용률
    @Bean
    public Job seatIdUsageJob(JobRepository jobRepository, Step seatIdUsageStep) {
        return new JobBuilder("seatIdUsageStep", jobRepository)
                .start(seatIdUsageStep)
                .build();
    }

    @Bean
    public Step seatIdUsageStep(JobRepository jobRepository, Tasklet seatIdUsageTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("seatIdUsageStep", jobRepository)
                .tasklet(seatIdUsageTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet seatIdUsageTasklet() {
        return (contribution, chunkContext) -> {
            shopService.calculateAndSaveSeatIdOccupancy();
            return RepeatStatus.FINISHED;
        };
    }


    /////////////
    //한사람당 평균 이용시간
    @Bean
    public Job userAvrUsageJob(JobRepository jobRepository, Step userAvrUsageStep) {
        return new JobBuilder("userAvrUsageStep", jobRepository)
                .start(userAvrUsageStep)
                .build();
    }

    @Bean
    public Step userAvrUsageStep(JobRepository jobRepository, Tasklet userAvrUsageTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("userAvrUsageStep", jobRepository)
                .tasklet(userAvrUsageTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet userAvrUsageTasklet() {
        return (contribution, chunkContext) -> {
            shopService.calculateAndSaveuserAvrUsage();
            return RepeatStatus.FINISHED;
        };
    }
}
