package com.notifymesh.workerservice.domain.service.delivery;

import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.domain.valueobject.Channel;
import com.notifymesh.workerservice.dto.NotificationEvent;

import java.util.List;

public interface NotificationSender {

    Channel channel();
    void send(NotificationEvent event, List<FileAttachment> attachments) throws Exception;
}
