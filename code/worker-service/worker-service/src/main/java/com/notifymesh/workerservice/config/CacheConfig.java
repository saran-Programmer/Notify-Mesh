package com.notifymesh.workerservice.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.notifymesh.workerservice.constants.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    private static final String KEY_PREFIX = "worker-service::";

    private final CacheProperties cacheProperties;

    private final ObjectMapper objectMapper;

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        String notifymeshBasePackage = "com.notifymesh";
        String javaTimePackage = "java.time";

        ObjectMapper cacheMapper = objectMapper.copy()
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType(notifymeshBasePackage)
                                .allowIfSubType(javaTimePackage)
                                .build(),
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY);

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .prefixCacheNameWith(KEY_PREFIX)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer(cacheMapper)))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put(CacheNames.TEMPLATE, base.entryTtl(resolveTtl(cacheProperties.getTemplate())));

        return RedisCacheManager.builder(factory)
                .withInitialCacheConfigurations(configs)
                .build();
    }

    private static RedisSerializer<Object> jsonSerializer(ObjectMapper mapper) {

        return new RedisSerializer<>() {

            @Override
            public byte[] serialize(Object value) throws SerializationException {

                if (value == null) {

                    return null;
                }
                try {
                    return mapper.writeValueAsBytes(value);
                } catch (IOException e) {
                    throw new SerializationException("Failed to serialize cache value", e);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {

                if (bytes == null || bytes.length == 0) {

                    return null;
                }
                try {
                    return mapper.readValue(bytes, Object.class);
                } catch (IOException e) {
                    throw new SerializationException("Failed to deserialize cache value", e);
                }
            }
        };
    }

    private Duration resolveTtl(long seconds) {

        return seconds == 0 ? Duration.ZERO : Duration.ofSeconds(seconds);
    }
}
