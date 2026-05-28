package com.notifymesh.workerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@EnableRetry
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsProperties awsProperties;

    @Bean
    S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(awsProperties.getRegion()))
                .build();
    }
}
