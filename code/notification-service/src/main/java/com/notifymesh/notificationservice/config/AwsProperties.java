package com.notifymesh.notificationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {

    private String region;
    private S3Properties s3 = new S3Properties();
    private LambdaProperties lambda = new LambdaProperties();
    private EventBridgeProperties eventbridge = new EventBridgeProperties();

    @Data
    public static class S3Properties {
        private String bucketName;
    }

    @Data
    public static class LambdaProperties {
        private String schedulerArn;
    }

    @Data
    public static class EventBridgeProperties {
        private String roleArn;
    }
}
