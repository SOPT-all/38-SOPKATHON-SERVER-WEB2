package com.sopt.sopkathon_web2_server.domain.records.service;

import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordAnswerResponse;
import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordDetailResponse;
import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordItemResponse;
import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordsResponse;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecordsService {

    private static final long COMPLETED_ANSWER_COUNT = 2L;

    private final ParticipantRepository participantRepository;
    private final RoomQuestionRepository roomQuestionRepository;
    private final AnswerRepository answerRepository;
    private final String bucket;
    private final String region;

    public RecordsService(
            ParticipantRepository participantRepository,
            RoomQuestionRepository roomQuestionRepository,
            AnswerRepository answerRepository,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region}") String region
    ) {
        this.participantRepository = participantRepository;
        this.roomQuestionRepository = roomQuestionRepository;
        this.answerRepository = answerRepository;
        this.bucket = bucket;
        this.region = region;
    }

    public RecordsResponse getRecords(String browserToken) {
        Participant participant = findParticipant(browserToken);

        return new RecordsResponse(
                roomQuestionRepository.findAllByRoomIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(
                                participant.getRoom().getId()
                        ).stream()
                        .filter(this::isCompletedByBothParticipants)
                        .map(this::toRecordItemResponse)
                        .toList()
        );
    }

    public RecordDetailResponse getRecordDetail(Long roomQuestionId, String browserToken) {
        Participant participant = findParticipant(browserToken);
        RoomQuestion roomQuestion = findCompletedRecord(roomQuestionId, participant);
        List<Answer> answers = answerRepository.findAllByRoomQuestionId(roomQuestion.getId());

        if (answers.size() != COMPLETED_ANSWER_COUNT) {
            throw new CustomException(ErrorCode.RECORD_NOT_FOUND);
        }

        return new RecordDetailResponse(
                roomQuestion.getId(),
                roomQuestion.getQuestion().getContent(),
                roomQuestion.getCompletedAt(),
                answers.stream()
                        .sorted(Comparator.comparingInt(this::getAnswerRoleOrder))
                        .map(answer -> toRecordAnswerResponse(answer, participant))
                        .toList()
        );
    }

    private Participant findParticipant(String browserToken) {
        if (!StringUtils.hasText(browserToken)) {
            throw new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT);
        }

        return participantRepository.findByBrowserTokenHash(hashToken(browserToken))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT));
    }

    private boolean isCompletedByBothParticipants(RoomQuestion roomQuestion) {
        return answerRepository.countByRoomQuestionId(roomQuestion.getId()) == COMPLETED_ANSWER_COUNT;
    }

    private RoomQuestion findCompletedRecord(Long roomQuestionId, Participant participant) {
        if (roomQuestionId == null) {
            throw new CustomException(ErrorCode.RECORD_NOT_FOUND);
        }

        RoomQuestion roomQuestion = roomQuestionRepository.findByIdAndRoomId(roomQuestionId, participant.getRoom().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        if (roomQuestion.getCompletedAt() == null) {
            throw new CustomException(ErrorCode.RECORD_NOT_FOUND);
        }

        return roomQuestion;
    }

    private RecordItemResponse toRecordItemResponse(RoomQuestion roomQuestion) {
        return new RecordItemResponse(
                roomQuestion.getId(),
                roomQuestion.getQuestion().getContent(),
                roomQuestion.getCompletedAt()
        );
    }

    private RecordAnswerResponse toRecordAnswerResponse(Answer answer, Participant participant) {
        return new RecordAnswerResponse(
                answer.getParticipant().getRole(),
                createVideoUrl(answer.getVideoKey()),
                answer.getCreatedAt(),
                answer.getParticipant().getId().equals(participant.getId())
        );
    }

    private int getAnswerRoleOrder(Answer answer) {
        return answer.getParticipant().getRole() == ParticipantRole.PARENT ? 0 : 1;
    }

    private String createVideoUrl(String videoKey) {
        String normalizedVideoKey = videoKey.startsWith("/") ? videoKey.substring(1) : videoKey;

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + normalizedVideoKey;
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
