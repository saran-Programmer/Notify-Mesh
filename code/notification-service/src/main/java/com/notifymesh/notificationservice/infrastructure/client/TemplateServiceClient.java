package com.notifymesh.notificationservice.infrastructure.client;

import com.notifymesh.notificationservice.constants.CacheNames;
import com.notifymesh.notificationservice.dto.TemplateMetadata;
import com.notifymesh.notificationservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TemplateServiceClient {

    private final WebClient templateServiceWebClient;

    @Cacheable(cacheNames = CacheNames.TEMPLATE_EXISTS, key = "#templateName + '::' + #channelType")
    public TemplateMetadata templateExists(String templateName, String channelType) {

        return templateServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/templates/{templateName}")
                        .queryParam("channelType", channelType)
                        .queryParam("isDeletedIncluded", false)
                        .build(templateName))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).map(body ->
                                new NotFoundException("template '" + templateName + "' not found for channel '" + channelType + "'")))
                .bodyToMono(TemplateMetadata.class)
                .block();
    }
}
