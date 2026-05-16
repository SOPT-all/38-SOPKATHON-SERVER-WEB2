package com.sopt.sopkathon_web2_server.domain.upload.service;

import com.sopt.sopkathon_web2_server.domain.upload.dto.request.PresignedUrlRequest;
import com.sopt.sopkathon_web2_server.domain.upload.dto.response.PresignedUrlResponse;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class UploadService {

    private static final Duration SIGNATURE_DURATION = Duration.ofMinutes(10);
    private static final String UPLOAD_PREFIX = "uploads/";

    private final S3Presigner s3Presigner;
    private final String bucket;

    public UploadService(
            S3Presigner s3Presigner,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
    }

    public PresignedUrlResponse createPresignedUrl(PresignedUrlRequest request) {
        validateRequest(request);

        String key = createObjectKey(request.fileName());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(request.contentType())
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(SIGNATURE_DURATION)
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return new PresignedUrlResponse(presignedRequest.url().toString(), key);
    }

    private void validateRequest(PresignedUrlRequest request) {
        if (request == null
                || !StringUtils.hasText(request.fileName())
                || !StringUtils.hasText(request.contentType())) {
            throw new CustomException(ErrorCode.INVALID_UPLOAD_REQUEST);
        }
    }

    private String createObjectKey(String fileName) {
        return UPLOAD_PREFIX + UUID.randomUUID() + "-" + sanitizeFileName(fileName);
    }

    private String sanitizeFileName(String fileName) {
        String normalizedPath = fileName.trim().replace("\\", "/");
        String baseName = normalizedPath.substring(normalizedPath.lastIndexOf('/') + 1);

        return baseName.replaceAll("\\s+", "-")
                .replaceAll("[^\\p{L}\\p{N}._-]", "-");
    }
}
