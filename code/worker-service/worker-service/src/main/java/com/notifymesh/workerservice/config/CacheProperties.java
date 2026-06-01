package com.notifymesh.workerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis.cache.ttl")
@Data
public class CacheProperties {

    private long template;
}
