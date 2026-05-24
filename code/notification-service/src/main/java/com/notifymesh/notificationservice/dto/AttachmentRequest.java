package com.notifymesh.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentRequest {

    @NotBlank
    private String s3Key;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    @Positive
    private Integer size;
}
