package com.sopt.sopkathon_web2_server.domain.upload.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadServiceTest {

    private S3Presigner s3Presigner;

    @BeforeEach
    void setUp() {
        s3Presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test-access-key", "test-secret-key")
                ))
                .build();
    }

    @AfterEach
    void tearDown() {
        s3Presigner.close();
    }

    @Test
    void createPresignedUrlGeneratesUploadsKeyAndPutUrl() {
        UploadService uploadService = new UploadService(s3Presigner, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest("profile image.png", "image/png");

        PresignedUrlResponse response = uploadService.createPresignedUrl(request);

        assertThat(response.key())
                .startsWith("uploads/")
                .endsWith("-profile-image.png");
        assertThat(response.uploadUrl())
                .contains("test-bucket")
                .contains("X-Amz-Algorithm=AWS4-HMAC-SHA256")
                .contains("X-Amz-Signature");
    }

    @Test
    void createPresignedUrlRejectsBlankFileName() {
        UploadService uploadService = new UploadService(s3Presigner, "test-bucket");
        PresignedUrlRequest request = new PresignedUrlRequest(" ", "image/png");

        assertThatThrownBy(() -> uploadService.createPresignedUrl(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_UPLOAD_REQUEST);
    }
}
