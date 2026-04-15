package com.uniremington.alparque.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    
    @Bean
    public Step syncStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("syncStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Job syncJob(JobRepository jobRepository, Step syncStep) {
        return new JobBuilder("syncJob", jobRepository)
            .start(syncStep)
            .build();
    }
}
