package com.sopt.sopkathon_web2_server.domain.upload.service;

import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class UploadService {

    private static final Duration SIGNATURE_DURATION = Duration.ofMinutes(10);
    private static final String UPLOAD_PREFIX = "uploads/";
    private static final String BEARER_PREFIX = "Bearer ";

    private final S3Presigner s3Presigner;
    private final ParticipantRepository participantRepository;
    private final String bucket;

    public UploadService(
            S3Presigner s3Presigner,
            ParticipantRepository participantRepository,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.s3Presigner = s3Presigner;
        this.participantRepository = participantRepository;
        this.bucket = bucket;
    }

    public PresignedUrlResponse createPresignedUrl(PresignedUrlRequest request, String authorizationHeader) {
        validateBrowserToken(authorizationHeader);
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
                || !StringUtils.hasText(request.contentType())
                || !isSupportedContentType(request.contentType())) {
            throw new CustomException(ErrorCode.INVALID_UPLOAD_REQUEST);
        }
    }

    private void validateBrowserToken(String authorizationHeader) {
        String browserToken = extractBrowserToken(authorizationHeader);
        String browserTokenHash = hashToken(browserToken);

        if (!participantRepository.existsByBrowserTokenHash(browserTokenHash)) {
            throw new CustomException(ErrorCode.INVALID_BROWSER_TOKEN);
        }
    }

    private String extractBrowserToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new CustomException(ErrorCode.INVALID_BROWSER_TOKEN);
        }

        String browserToken = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(browserToken)) {
            throw new CustomException(ErrorCode.INVALID_BROWSER_TOKEN);
        }

        return browserToken;
    }

    private String createObjectKey(String fileName) {
        return UPLOAD_PREFIX + UUID.randomUUID() + "-" + sanitizeFileName(fileName);
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.startsWith("image/") || contentType.startsWith("video/");
    }

    private String hashToken(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedToken = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashedToken);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", e);
        }
    }

    private String sanitizeFileName(String fileName) {
        String normalizedPath = fileName.trim().replace("\\", "/");
        String baseName = normalizedPath.substring(normalizedPath.lastIndexOf('/') + 1);

        return baseName.replaceAll("\\s+", "-")
                .replaceAll("[^\\p{L}\\p{N}._-]", "-");
    }
}
