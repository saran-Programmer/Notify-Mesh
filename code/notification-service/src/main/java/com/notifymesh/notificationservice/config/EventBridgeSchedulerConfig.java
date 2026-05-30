package com.notifymesh.notificationservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.scheduler.SchedulerClient;

@Configuration
@RequiredArgsConstructor
public class EventBridgeSchedulerConfig {

    private final AwsProperties awsProperties;

    @Bean
    SchedulerClient schedulerClient() {
        return SchedulerClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(awsProperties.getRegion()))
                .build();
    }
}
