package com.notifymesh.notificationservice.exception;

import java.time.LocalDateTime;

public abstract class BaseException extends RuntimeException {

    private final LocalDateTime dateTime;

    protected BaseException(String message) {
        super(message);
        this.dateTime = LocalDateTime.now();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
