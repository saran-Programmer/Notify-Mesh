package com.notifymesh.notificationservice.dto;

import com.notifymesh.notificationservice.domain.valueobject.Channel;
import com.notifymesh.notificationservice.domain.valueobject.DeliveryType;
import com.notifymesh.notificationservice.domain.valueobject.Mode;
import com.notifymesh.notificationservice.domain.valueobject.Priority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest {

    @NotBlank
    private String recipient;

    @NotNull
    private Channel channel;

    @NotNull
    private Mode mode;

    @NotNull
    private DeliveryType deliveryType;

    private String subject;

    private String templateName;

    private HashMap<String, Object> parameter;

    private LocalDateTime scheduledAt;

    @NotBlank
    private String externalId;

    @NotNull
    private Priority priority;

    @NotNull
    @Min(0)
    private Integer maxRetries;

    private String content;

    @Valid
    private List<AttachmentRequest> attachmentIds;
}
