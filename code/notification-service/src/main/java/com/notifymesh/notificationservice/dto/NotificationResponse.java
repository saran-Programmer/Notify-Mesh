package com.notifymesh.notificationservice.dto;

import com.notifymesh.notificationservice.domain.valueobject.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private String externalId;

    private NotificationStatus status;
}
