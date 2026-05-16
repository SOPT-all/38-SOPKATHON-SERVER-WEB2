package com.sopt.sopkathon_web2_server.domain.upload.service;

import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.upload.dto.request.PresignedUrlRequest;
import com.sopt.sopkathon_web2_server.domain.upload.dto.response.PresignedUrlResponse;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UploadServiceTest {

    private S3Presigner s3Presigner;
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        s3Presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test-access-key", "test-secret-key")
                ))
                .build();
        participantRepository = mock(ParticipantRepository.class);
    }

    @AfterEach
    void tearDown() {
        s3Presigner.close();
    }

    @Test
    void createPresignedUrlGeneratesUploadsKeyAndPutUrl() {
        when(participantRepository.existsByBrowserTokenHash(hashToken("valid-browser-token"))).thenReturn(true);
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("profile image.png", "image/png");

        PresignedUrlResponse response = uploadService.createPresignedUrl(request, "Bearer valid-browser-token");

        assertThat(response.key())
                .startsWith("uploads/")
                .endsWith("-profile-image.png");
        assertThat(response.uploadUrl())
                .contains("test-bucket")
                .contains("X-Amz-Algorithm=AWS4-HMAC-SHA256")
                .contains("X-Amz-Signature");
    }

    @Test
    void createPresignedUrlAllowsVideoContentType() {
        when(participantRepository.existsByBrowserTokenHash(hashToken("valid-browser-token"))).thenReturn(true);
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("answer video.mp4", "video/mp4");

        PresignedUrlResponse response = uploadService.createPresignedUrl(request, "Bearer valid-browser-token");

        assertThat(response.key())
                .startsWith("uploads/")
                .endsWith("-answer-video.mp4");
    }

    @Test
    void createPresignedUrlRejectsBlankFileName() {
        when(participantRepository.existsByBrowserTokenHash(hashToken("valid-browser-token"))).thenReturn(true);
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest(" ", "image/png");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request, "Bearer valid-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_UPLOAD_REQUEST);
    }

    @Test
    void createPresignedUrlRejectsBlankContentType() {
        when(participantRepository.existsByBrowserTokenHash(hashToken("valid-browser-token"))).thenReturn(true);
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("answer.png", " ");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request, "Bearer valid-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_UPLOAD_REQUEST);
    }

    @Test
    void createPresignedUrlRejectsUnsupportedContentType() {
        when(participantRepository.existsByBrowserTokenHash(hashToken("valid-browser-token"))).thenReturn(true);
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("answer.txt", "text/plain");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request, "Bearer valid-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_UPLOAD_REQUEST);
    }

    @Test
    void createPresignedUrlRejectsMissingAuthorizationHeader() {
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("answer.png", "image/png");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request, null))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_BROWSER_TOKEN);
    }

    @Test
    void createPresignedUrlRejectsNonBearerAuthorizationHeader() {
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("answer.png", "image/png");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request, "Token valid-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_BROWSER_TOKEN);
    }

    @Test
    void createPresignedUrlRejectsBlankBearerToken() {
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("answer.png", "image/png");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request, "Bearer "))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_BROWSER_TOKEN);
    }

    @Test
    void createPresignedUrlRejectsUnknownBrowserToken() {
        when(participantRepository.existsByBrowserTokenHash(hashToken("unknown-browser-token"))).thenReturn(false);
        UploadService uploadService = new UploadService(s3Presigner, participantRepository, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("answer.png", "image/png");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request, "Bearer unknown-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_BROWSER_TOKEN);
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
}
