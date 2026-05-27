package com.notifymesh.workerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding for {@code aws.*} properties in application.yaml.
 * Registered automatically via {@code @ConfigurationPropertiesScan}.
 */
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {

    /** AWS region, e.g. {@code ap-south-1}. */
    private String region;

    private S3Properties s3 = new S3Properties();

    @Data
    public static class S3Properties {

        /** S3 bucket that stores notification attachments. */
        private String bucketName;
    }
}
