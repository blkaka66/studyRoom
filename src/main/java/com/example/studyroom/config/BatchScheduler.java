package com.example.studyroom.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.UUID;

@Configuration
@EnableScheduling
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job shopUsageHourlyJob;
    private final Job shopUsageDailyJob;
    private final Job shopDailyPaymentJob;
    private final Job userAvrUsageJob;
    private final Job saveCustomerStatsJob;
    public BatchScheduler(JobLauncher jobLauncher, Job shopUsageHourlyJob, Job shopUsageDailyJob,
                           Job userAvrUsageJob, Job shopDailyPaymentJob, Job saveCustomerStatsJob) {
        this.jobLauncher = jobLauncher;
        this.shopUsageHourlyJob = shopUsageHourlyJob;
        this.shopUsageDailyJob = shopUsageDailyJob;
        this.shopDailyPaymentJob = shopDailyPaymentJob;
        this.userAvrUsageJob = userAvrUsageJob;
        this.saveCustomerStatsJob=saveCustomerStatsJob;
    }



    @Scheduled(cron = "0 0 * * * ?") // 매 시간 정각에 실행
    public void runShopHourlyUsageJob() throws Exception {
        // UUID를 사용하여 고유한 JobParameters 생성
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("jobId", UUID.randomUUID().toString())  // 고유 ID 생성(job 파라미터는 고유해야하히때문에 uuid 추가.
                // 고유하지않으면 이미 실행되었다고 판단해서 하루에한번만 실행될 위험있음)
                .addDate("date", new Date())  // 현재 시간 추가
                .toJobParameters();
        jobLauncher.run(shopUsageHourlyJob, jobParameters);
    }


    // 매일 23시 59분에 실행
    @Scheduled(cron = "0 59 23 * * ?")
    public void runShopDailyUsageJob() throws Exception {
        // UUID를 사용하여 고유한 JobParameters 생성
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("jobId", UUID.randomUUID().toString())  // 고유 ID 생성
                .addDate("date", new Date())  // 현재 시간 추가
                .toJobParameters();
        // 비동기 실행
        runShopUsageJobAsync(jobParameters);
        runUserAvrUsageJobAsync(jobParameters);
        runShopDailyPaymentJobAsync(jobParameters);
        saveCustomerStatsJobAsync(jobParameters);
    }





    @Async
    public void runShopUsageJobAsync(JobParameters jobParameters) throws Exception {
        jobLauncher.run(shopUsageDailyJob, jobParameters);
    }

    @Async
    public void runUserAvrUsageJobAsync(JobParameters jobParameters) throws Exception {
        jobLauncher.run(userAvrUsageJob, jobParameters);
    }

    @Async
    public void runShopDailyPaymentJobAsync(JobParameters jobParameters) throws Exception {
        jobLauncher.run(shopDailyPaymentJob, jobParameters);
    }

    @Async
    public void saveCustomerStatsJobAsync(JobParameters jobParameters) throws Exception {
        jobLauncher.run(saveCustomerStatsJob, jobParameters);
    }

}
