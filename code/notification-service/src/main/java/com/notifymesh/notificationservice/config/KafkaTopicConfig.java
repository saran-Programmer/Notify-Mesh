package com.notifymesh.notificationservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private static final String SEVEN_DAYS_MS = "604800000";

    private final KafkaProperties kafkaProperties;

    @Bean
    NewTopic highPriorityTopic() {
        return TopicBuilder.name(kafkaProperties.getTopics().getHigh())
                .partitions(5)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, SEVEN_DAYS_MS)
                .build();
    }

    @Bean
    NewTopic mediumPriorityTopic() {
        return TopicBuilder.name(kafkaProperties.getTopics().getMedium())
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, SEVEN_DAYS_MS)
                .build();
    }

    @Bean
    NewTopic lowPriorityTopic() {
        return TopicBuilder.name(kafkaProperties.getTopics().getLow())
                .partitions(2)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, SEVEN_DAYS_MS)
                .build();
    }
}
