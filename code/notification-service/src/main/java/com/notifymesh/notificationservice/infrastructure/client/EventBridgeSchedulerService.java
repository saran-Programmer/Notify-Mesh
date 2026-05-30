package com.notifymesh.notificationservice.infrastructure.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifymesh.notificationservice.config.AwsProperties;
import com.notifymesh.notificationservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.ActionAfterCompletion;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleRequest;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindow;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindowMode;
import software.amazon.awssdk.services.scheduler.model.Target;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EventBridgeSchedulerService {

    private static final DateTimeFormatter SCHEDULE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final SchedulerClient schedulerClient;
    private final AwsProperties awsProperties;
    private final ObjectMapper objectMapper;

    public void schedule(Long notificationId, LocalDateTime scheduledAt, NotificationEvent event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to serialize notification event for id " + notificationId, e);
        }

        Target target = Target.builder()
                .arn(awsProperties.getLambda().getSchedulerArn())
                .roleArn(awsProperties.getEventbridge().getRoleArn())
                .input(payload)
                .build();

        schedulerClient.createSchedule(CreateScheduleRequest.builder()
                .name("notifymesh-scheduled-" + notificationId)
                .scheduleExpression("at(" + scheduledAt.format(SCHEDULE_FORMATTER) + ")")
                .flexibleTimeWindow(FlexibleTimeWindow.builder()
                        .mode(FlexibleTimeWindowMode.OFF)
                        .build())
                .target(target)
                .actionAfterCompletion(ActionAfterCompletion.DELETE)
                .build());
    }
}
