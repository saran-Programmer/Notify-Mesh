package com.notifymesh.notificationservice.dto;

import com.notifymesh.notificationservice.domain.valueobject.Channel;
import com.notifymesh.notificationservice.domain.valueobject.Mode;
import com.notifymesh.notificationservice.domain.valueobject.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {

    private Long id;
    private Channel channel;
    private Priority priority;
    private String recipient;
    private String subject;
    private String content;
    private String templateName;
    private Map<String, String> parameters;
    private Integer retryCount;
    private Integer maxRetries;
    private List<String> attachments;
    private Mode mode;
    private Integer baseDelaySeconds;
}
