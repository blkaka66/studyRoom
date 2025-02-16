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
    //한사람당 평균 이용시간 & 하루당 좌석 id별 누적 이용시간
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
            shopService.calculateAndSaveUsageStatistics();
            return RepeatStatus.FINISHED;
        };
    }


    /////
    //하루당 shopid별 총 결제액(시간권,기간권 별로 총 결제액 따로 보여줌)

    @Bean
    public Job shopDailyPaymentJob(JobRepository jobRepository, Step shopDailyPaymentStep) {
        return new JobBuilder("ShopDailyPaymentStep", jobRepository)
                .start(shopDailyPaymentStep)
                .build();
    }

    @Bean
    public Step shopDailyPaymentStep(JobRepository jobRepository, Tasklet shopDailyPaymentTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("ShopDailyPaymentStep", jobRepository)
                .tasklet(shopDailyPaymentTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet shopDailyPaymentTasklet() {
        return (contribution, chunkContext) -> {
            shopService.calculateAndSaveShopDailyPayment();
            return RepeatStatus.FINISHED;
        };
    }

    /////
    //shopid별 총 고객수

    @Bean
    public Job saveCustomerStatsJob(JobRepository jobRepository, Step saveCustomerStatsStep) {
        return new JobBuilder("saveCustomerStatsStep", jobRepository)
                .start(saveCustomerStatsStep)
                .build();
    }

    @Bean
    public Step saveCustomerStatsStep(JobRepository jobRepository, Tasklet saveCustomerStatsTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("saveCustomerStatsStep", jobRepository)
                .tasklet(saveCustomerStatsTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet saveCustomerStatsTasklet() {
        return (contribution, chunkContext) -> {
            shopService.calculateAndSaveCustomerStats();
            return RepeatStatus.FINISHED;
        };
    }

}
