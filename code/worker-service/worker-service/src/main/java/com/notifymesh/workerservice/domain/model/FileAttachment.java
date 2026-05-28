package com.notifymesh.workerservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileAttachment {

    private String fileName;

    private byte[] fileBytes;
}
