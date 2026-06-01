package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.constants.CacheNames;
import com.notifymesh.workerservice.dto.TemplateResponse;
import com.notifymesh.workerservice.infrastructure.client.TemplateServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateCacheService {

    private final TemplateServiceClient templateServiceClient;

    @Cacheable(cacheNames = CacheNames.TEMPLATE, key = "#templateName + '::' + #channelType")
    public TemplateResponse getTemplate(String templateName, String channelType) {

        return templateServiceClient.fetchTemplate(templateName, channelType);
    }
}
