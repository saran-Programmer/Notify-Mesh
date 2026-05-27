package com.notifymesh.workerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Provides a singleton {@link S3Client} bean wired from {@link AwsProperties}.
 * Credentials are resolved via the AWS default-credentials chain
 * (env vars → ~/.aws/credentials → IAM role).
 *
 * <p>{@code @EnableRetry} is declared here so retry support is scoped to the
 * S3 / attachment infrastructure rather than being spread to unrelated beans.
 */
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
