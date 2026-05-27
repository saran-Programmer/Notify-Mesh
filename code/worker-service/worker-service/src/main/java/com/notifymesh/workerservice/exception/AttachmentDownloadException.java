package com.notifymesh.workerservice.exception;

/**
 * Thrown when an S3 attachment cannot be downloaded, either because the key
 * does not exist / access is denied (non-retryable) or because all retry
 * attempts for a transient error have been exhausted.
 */
public class AttachmentDownloadException extends RuntimeException {

    public AttachmentDownloadException(String message) {
        super(message);
    }

    public AttachmentDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
