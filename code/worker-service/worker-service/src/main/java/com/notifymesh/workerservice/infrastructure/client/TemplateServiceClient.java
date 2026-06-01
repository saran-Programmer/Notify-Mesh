package com.notifymesh.workerservice.infrastructure.client;

import com.notifymesh.workerservice.dto.TemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TemplateServiceClient {

    private final WebClient webClient;

    @Value("${template-service.base-url}")
    private String baseUrl;

    public TemplateResponse fetchTemplate(String templateName, String channelType) {

        return webClient.get()
                .uri(baseUrl + "/api/v1/templates/{templateName}?channelType={channelType}&isDeletedIncluded=true",
                        templateName, channelType)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).map(body ->
                                new RuntimeException("template-service returned error for template '" + templateName
                                        + "' and channel '" + channelType + "': " + body)))
                .bodyToMono(TemplateResponse.class)
                .block();
    }
}
