package com.sopt.sopkathon_web2_server.domain.records.service;

import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordItemResponse;
import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordsResponse;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
@Transactional(readOnly = true)
public class RecordsService {

    private static final long COMPLETED_ANSWER_COUNT = 2L;

    private final ParticipantRepository participantRepository;
    private final RoomQuestionRepository roomQuestionRepository;
    private final AnswerRepository answerRepository;

    public RecordsService(
            ParticipantRepository participantRepository,
            RoomQuestionRepository roomQuestionRepository,
            AnswerRepository answerRepository
    ) {
        this.participantRepository = participantRepository;
        this.roomQuestionRepository = roomQuestionRepository;
        this.answerRepository = answerRepository;
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

    private RecordItemResponse toRecordItemResponse(RoomQuestion roomQuestion) {
        return new RecordItemResponse(
                roomQuestion.getId(),
                roomQuestion.getQuestion().getContent(),
                roomQuestion.getCompletedAt()
        );
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
