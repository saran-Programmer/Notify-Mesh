package com.notifymesh.workerservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a downloaded file attachment — its name and raw bytes.
 * Produced by {@link com.notifymesh.workerservice.domain.service.AttachmentService}
 * after fetching each S3 object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileAttachment {

    /** Original file name extracted from the S3 key. */
    private String fileName;

    /** Raw content of the file as a byte array. */
    private byte[] fileBytes;
}
