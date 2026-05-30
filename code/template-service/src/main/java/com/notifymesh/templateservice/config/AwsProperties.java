package com.notifymesh.templateservice.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwsProperties {

    private String region;
    private DynamoDb dynamodb;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DynamoDb {
        private String tableName;
    }
}
