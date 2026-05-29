package com.notifymesh.notificationservice.infrastructure.client;

import com.notifymesh.notificationservice.config.AwsProperties;
import com.notifymesh.notificationservice.exception.AttachmentReadException;
import com.notifymesh.notificationservice.exception.S3BucketNotFoundException;
import com.notifymesh.notificationservice.exception.S3UploadException;
import com.notifymesh.notificationservice.exception.S3UploadRetryExhaustedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    @Retryable(
            retryFor = { S3Exception.class, SdkClientException.class, IOException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public String upload(MultipartFile file) throws IOException {
        String s3Key = "attachments/" + UUID.randomUUID() + "/" + file.getOriginalFilename();
        byte[] bytes = readBytes(file);

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(awsProperties.getS3().getBucketName())
                            .key(s3Key)
                            .contentType(file.getContentType())
                            .contentLength((long) bytes.length)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
        } catch (NoSuchBucketException e) {
            throw new S3BucketNotFoundException(
                    "S3 bucket '" + awsProperties.getS3().getBucketName() + "' does not exist - check aws.s3.bucket-name in config");
        } catch (S3Exception e) {
            if (e.statusCode() / 100 == 4) {
                throw new S3UploadException(
                        "S3 rejected upload of '" + file.getOriginalFilename() + "' (HTTP " + e.statusCode() + "): "
                                + e.awsErrorDetails().errorMessage());
            }
            log.warn("Transient S3 error uploading '{}' (HTTP {}), retrying", file.getOriginalFilename(), e.statusCode());
            throw e;
        } catch (SdkClientException e) {
            log.warn("Connectivity error uploading '{}', retrying: {}", file.getOriginalFilename(), e.getMessage());
            throw e;
        }

        return s3Key;
    }

    public String getObjectUrl(String s3Key) {
        return "https://" + awsProperties.getS3().getBucketName()
                + ".s3." + awsProperties.getRegion()
                + ".amazonaws.com/" + s3Key;
    }

    @Recover
    public String recoverUpload(SdkException e, MultipartFile file) {
        throw new S3UploadRetryExhaustedException(
                "Upload of '" + file.getOriginalFilename() + "' failed after 3 attempts: " + e.getMessage());
    }

    @Recover
    public String recoverUpload(IOException e, MultipartFile file) {
        throw new AttachmentReadException(
                "Cannot read bytes from file '" + file.getOriginalFilename() + "' after 3 attempts: " + e.getMessage());
    }

    private byte[] readBytes(MultipartFile file) throws IOException {
        return file.getBytes();
    }
}
