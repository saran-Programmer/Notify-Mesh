package com.notifymesh.notificationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {

    private String region;
    private S3Properties s3 = new S3Properties();

    @Data
    public static class S3Properties {
        private String bucketName;
    }
}
