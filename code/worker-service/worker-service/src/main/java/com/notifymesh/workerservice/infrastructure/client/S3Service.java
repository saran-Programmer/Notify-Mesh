package com.notifymesh.workerservice.infrastructure.client;

import com.notifymesh.workerservice.config.AwsProperties;
import com.notifymesh.workerservice.exception.AttachmentDownloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Thin infrastructure wrapper around the AWS S3 SDK.
 * Responsible for a single concern: downloading the raw bytes
 * of an S3 object identified by its key.
 *
 * <p>Transient errors (5xx responses, connectivity failures) are retried up to
 * three times with exponential back-off, mirroring the upload retry policy in
 * the notification-service. Non-retryable errors (404, 403, …) are wrapped in
 * {@link AttachmentDownloadException} and propagated immediately.
 *
 * <p>Called exclusively by
 * {@link com.notifymesh.workerservice.domain.service.AttachmentService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    // ------------------------------------------------------------------ download

    /**
     * Downloads the S3 object at {@code s3Key} and returns its contents as a
     * raw byte array.
     *
     * <p>Retry policy (matches notification-service upload policy):
     * <ul>
     *   <li>Retries on transient {@link S3Exception} (HTTP 5xx) and
     *       {@link SdkClientException} (network / connectivity).</li>
     *   <li>4xx errors ({@link NoSuchKeyException}, access-denied, …) are
     *       thrown as {@link AttachmentDownloadException} immediately — no retry.</li>
     * </ul>
     *
     * @param s3Key the S3 object key (e.g. {@code attachments/uuid/file.pdf})
     * @return the raw file bytes
     * @throws AttachmentDownloadException if the key does not exist, access is
     *         denied, or all retries for a transient error are exhausted
     */
    @Retryable(
            retryFor  = { S3Exception.class, SdkClientException.class },
            maxAttempts = 3,
            backoff   = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public byte[] downloadFile(String s3Key) {
        log.debug("Downloading S3 object — bucket={}, key={}",
                awsProperties.getS3().getBucketName(), s3Key);

        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(awsProperties.getS3().getBucketName())
                            .key(s3Key)
                            .build()
            );

            byte[] bytes = response.asByteArray();
            log.debug("Downloaded S3 object — key={}, bytes={}", s3Key, bytes.length);
            return bytes;

        } catch (NoSuchKeyException e) {
            // 404 — key does not exist; retrying will never help
            throw new AttachmentDownloadException(
                    "S3 object not found: '" + s3Key + "'");

        } catch (S3Exception e) {
            if (e.statusCode() / 100 == 4) {
                // other 4xx (forbidden, invalid request, …) — fail immediately
                throw new AttachmentDownloadException(
                        "S3 rejected download of '" + s3Key + "' (HTTP " + e.statusCode() + "): "
                                + e.awsErrorDetails().errorMessage());
            }
            // 5xx — transient; re-throw so @Retryable kicks in
            log.warn("Transient S3 error downloading '{}' (HTTP {}), retrying",
                    s3Key, e.statusCode());
            throw e;

        } catch (SdkClientException e) {
            // connectivity issue — transient; re-throw so @Retryable kicks in
            log.warn("Connectivity error downloading '{}', retrying: {}", s3Key, e.getMessage());
            throw e;
        }
    }

    // ------------------------------------------------------------------ recovery

    /**
     * Called by Spring Retry after all {@link #downloadFile} attempts are
     * exhausted for a transient {@link SdkException}.
     */
    @Recover
    public byte[] recoverDownload(SdkException e, String s3Key) {
        throw new AttachmentDownloadException(
                "Download of '" + s3Key + "' failed after 3 attempts: " + e.getMessage(), e);
    }
}
