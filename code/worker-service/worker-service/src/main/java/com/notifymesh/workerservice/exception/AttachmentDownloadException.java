package com.notifymesh.workerservice.exception;

public class AttachmentDownloadException extends RuntimeException {

    public AttachmentDownloadException(String message) {
        super(message);
    }

    public AttachmentDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
