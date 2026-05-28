package com.notifymesh.workerservice.infrastructure.client;

import com.notifymesh.workerservice.config.AwsProperties;
import com.notifymesh.workerservice.exception.AttachmentDownloadException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    @Retryable(
            retryFor    = { S3Exception.class, SdkClientException.class },
            maxAttempts = 3,
            backoff     = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public byte[] downloadFile(String s3Key) {
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(awsProperties.getS3().getBucketName())
                            .key(s3Key)
                            .build()
            );

            return response.asByteArray();

        } catch (NoSuchKeyException e) {
            throw new AttachmentDownloadException(
                    "S3 object not found: '" + s3Key + "'");

        } catch (S3Exception e) {
            if (e.statusCode() / 100 == 4) {
                throw new AttachmentDownloadException(
                        "S3 rejected download of '" + s3Key + "' (HTTP " + e.statusCode() + "): "
                                + e.awsErrorDetails().errorMessage());
            }
            throw e;

        } catch (SdkClientException e) {
            throw e;
        }
    }

    @Recover
    public byte[] recoverDownload(SdkException e, String s3Key) {
        throw new AttachmentDownloadException(
                "Download of '" + s3Key + "' failed after 3 attempts: " + e.getMessage(), e);
    }
}
