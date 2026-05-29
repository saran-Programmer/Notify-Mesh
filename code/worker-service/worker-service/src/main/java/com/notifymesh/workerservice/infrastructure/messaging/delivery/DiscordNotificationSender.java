package com.notifymesh.workerservice.infrastructure.messaging.delivery;

import com.notifymesh.workerservice.config.DiscordProperties;
import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.domain.service.delivery.NotificationSender;
import com.notifymesh.workerservice.domain.valueobject.Channel;
import com.notifymesh.workerservice.dto.NotificationEvent;
import com.notifymesh.workerservice.exception.DiscordDeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationSender implements NotificationSender {

    private static final String DISCORD_API_BASE = "https://discord.com/api/v10";

    private final WebClient webClient;
    private final DiscordProperties discordProperties;

    @Override
    public Channel channel() {
        return Channel.DISCORD;
    }

    @Override
    public void send(NotificationEvent event, List<FileAttachment> attachments) {
        String channelId = event.getRecipient();
        String url = DISCORD_API_BASE + "/channels/" + channelId + "/messages";
        String authHeader = "Bot " + discordProperties.getBotToken();

        if (attachments.isEmpty()) {
            sendTextMessage(url, authHeader, event.getContent());
        } else {
            sendMultipartMessage(url, authHeader, event.getContent(), attachments);
        }
    }

    private void sendTextMessage(String url, String authHeader, String content) {
        webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("content", content))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class).map(errorBody ->
                                new DiscordDeliveryException(response.statusCode().value(), errorBody)))
                .toBodilessEntity()
                .block();
    }

    private void sendMultipartMessage(String url, String authHeader, String content, List<FileAttachment> attachments) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("payload_json", Map.of("content", content)).contentType(MediaType.APPLICATION_JSON);

        for (int i = 0; i < attachments.size(); i++) {
            FileAttachment attachment = attachments.get(i);
            builder.part("files[" + i + "]", new ByteArrayResource(attachment.getFileBytes()))
                    .filename(attachment.getFileName());
        }

        webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class).map(errorBody ->
                                new DiscordDeliveryException(response.statusCode().value(), errorBody)))
                .toBodilessEntity()
                .block();
    }
}
