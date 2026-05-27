package com.notifymesh.workerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaProperties {

    private String bootstrapServers;
    private String consumerGroupId;
    private Topics topics = new Topics();

    @Data
    public static class Topics {
        private String high;
        private String medium;
        private String low;
    }
}
