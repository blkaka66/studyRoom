//package com.example.studyroom.config;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.UUID;
////여긴 시작하자마자 실행되는 batch 코드 (현재는 테스트용)
//@Component
//public class BatchRunner implements CommandLineRunner {
//    private final JobLauncher jobLauncher;
//    private final Job shopUsageHourlyJob;
//    private final Job shopUsageDailyJob;
//
//    public BatchRunner(JobLauncher jobLauncher, Job shopUsageHourlyJob, Job shopUsageDailyJob) {
//        this.jobLauncher = jobLauncher;
//        this.shopUsageHourlyJob = shopUsageHourlyJob;
//        this.shopUsageDailyJob = shopUsageDailyJob;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // shopUsageHourlyJob 실행
//        JobParameters jobParametersHourly = new JobParametersBuilder()
//                .addString("jobId", UUID.randomUUID().toString())  // 고유 ID 생성
//                .addDate("date", new Date())  // 현재 시간 추가
//                .toJobParameters();
//        jobLauncher.run(shopUsageHourlyJob, jobParametersHourly);
//
//        // shopUsageDailyJob 실행
//        JobParameters jobParametersDaily = new JobParametersBuilder()
//                .addString("jobId", UUID.randomUUID().toString())  // 고유 ID 생성
//                .addDate("date", new Date())  // 현재 시간 추가
//                .toJobParameters();
//        jobLauncher.run(shopUsageDailyJob, jobParametersDaily);
//    }
//}
