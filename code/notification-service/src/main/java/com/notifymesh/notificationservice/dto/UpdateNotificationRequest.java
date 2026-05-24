package com.notifymesh.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateNotificationRequest {

    private String recipient;

    private String subject;

    private String content;

    private LocalDateTime scheduledAt;

    private String externalId;
}
