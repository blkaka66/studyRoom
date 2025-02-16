//package com.example.studyroom.config;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Qualifier;
//
//import java.util.Date;
//import java.util.UUID;
//
//@Component
//public class BatchRunner implements CommandLineRunner {
//    private final JobLauncher jobLauncher;
//
//    @Qualifier("shopUsageHourlyJob")
//    private final Job shopUsageHourlyJob;
//
//    @Qualifier("shopUsageDailyJob")
//    private final Job shopUsageDailyJob;
//
//    @Qualifier("shopDailyPaymentJob")
//    private final Job shopDailyPaymentJob;
//
//    @Qualifier("userAvrUsageJob")
//    private final Job userAvrUsageJob;
//
//    @Qualifier("saveCustomerStatsJob")
//    private final Job saveCustomerStatsJob;
//
//    public BatchRunner(JobLauncher jobLauncher,
//                       @Qualifier("shopUsageHourlyJob") Job shopUsageHourlyJob,
//                       @Qualifier("shopUsageDailyJob") Job shopUsageDailyJob,
//                       @Qualifier("shopDailyPaymentJob") Job shopDailyPaymentJob,
//                       @Qualifier("userAvrUsageJob") Job userAvrUsageJob,
//                       @Qualifier("saveCustomerStatsJob") Job saveCustomerStatsJob) {
//        this.jobLauncher = jobLauncher;
//        this.shopUsageHourlyJob = shopUsageHourlyJob;
//        this.shopUsageDailyJob = shopUsageDailyJob;
//        this.shopDailyPaymentJob = shopDailyPaymentJob;
//        this.userAvrUsageJob = userAvrUsageJob;
//        this.saveCustomerStatsJob = saveCustomerStatsJob;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 여러 작업을 반복적으로 실행하는 코드
//        runJob(shopUsageHourlyJob);
//        runJob(shopUsageDailyJob);
//        runJob(userAvrUsageJob);
//        runJob(shopDailyPaymentJob);
//        runJob(saveCustomerStatsJob);
//    }
//
//    private void runJob(Job job) throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("jobId", UUID.randomUUID().toString())  // 고유 ID 생성
//                .addDate("date", new Date())  // 현재 시간 추가
//                .toJobParameters();
//        jobLauncher.run(job, jobParameters);
//    }
//}
