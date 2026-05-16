package com.sopt.sopkathon_web2_server.domain.answers.service;

import com.sopt.sopkathon_web2_server.domain.answers.dto.request.CreateAnswerRequest;
import com.sopt.sopkathon_web2_server.domain.answers.dto.response.CreateAnswerResponse;
import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class AnswerService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int REQUIRED_ANSWER_COUNT = 2;
    private static final int STREAK_MESSAGE_MAX_DAY = 4;

    private final AnswerRepository answerRepository;
    private final ParticipantRepository participantRepository;
    private final RoomQuestionRepository roomQuestionRepository;

    public AnswerService(
            AnswerRepository answerRepository,
            ParticipantRepository participantRepository,
            RoomQuestionRepository roomQuestionRepository
    ) {
        this.answerRepository = answerRepository;
        this.participantRepository = participantRepository;
        this.roomQuestionRepository = roomQuestionRepository;
    }

    @Transactional
    public CreateAnswerResponse createAnswer(CreateAnswerRequest request, String authorizationHeader) {
        Participant participant = findParticipantByAuthorizationHeader(authorizationHeader);
        RoomQuestion roomQuestion = findRoomQuestion(request, participant);

        if (answerRepository.existsByRoomQuestionIdAndParticipantId(roomQuestion.getId(), participant.getId())) {
            throw new CustomException(ErrorCode.ANSWER_ALREADY_EXISTS);
        }

        Answer answer = answerRepository.save(new Answer(roomQuestion, participant, request.imageKey()));
        boolean isUnlocked = answerRepository.countByRoomQuestionId(roomQuestion.getId()) >= REQUIRED_ANSWER_COUNT;

        if (isUnlocked && roomQuestion.getCompletedAt() == null) {
            LocalDateTime completedAt = LocalDateTime.now();
            roomQuestion.updateCompletedAt(completedAt);
            roomQuestion.getRoom().updateLastCompletedAt(completedAt);
        }

        return new CreateAnswerResponse(
                answer.getId(),
                roomQuestion.getId(),
                answer.getImageKey(),
                isUnlocked,
                roomQuestion.getRoom().getCurrentStreakDay(),
                createCurrentStreakMessage(roomQuestion.getRoom().getCurrentStreakDay()),
                answer.getCreatedAt()
        );
    }

    private Participant findParticipantByAuthorizationHeader(String authorizationHeader) {
        String browserToken = extractBrowserToken(authorizationHeader);
        String browserTokenHash = hashToken(browserToken);

        return participantRepository.findByBrowserTokenHash(browserTokenHash)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BROWSER_TOKEN));
    }

    private RoomQuestion findRoomQuestion(CreateAnswerRequest request, Participant participant) {
        if (request == null || request.roomQuestionId() == null) {
            throw new CustomException(ErrorCode.QUESTION_NOT_FOUND);
        }

        Room room = participant.getRoom();

        return roomQuestionRepository.findByIdAndRoomId(request.roomQuestionId(), room.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
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

    private String hashToken(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedToken = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashedToken);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", e);
        }
    }

    private String createCurrentStreakMessage(Integer currentStreakDay) {
        return switch (Math.min(currentStreakDay, STREAK_MESSAGE_MAX_DAY)) {
            case 1 -> "아직은 머뭇거리는 중이에요.";
            case 2 -> "한 걸음씩 다가가는 중이에요.";
            case 3 -> "거리가 좁혀지고 있어요!";
            default -> "드디어 맞닿았어요!";
        };
    }
}
