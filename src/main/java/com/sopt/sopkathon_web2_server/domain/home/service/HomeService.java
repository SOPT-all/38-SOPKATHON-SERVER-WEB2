package com.sopt.sopkathon_web2_server.domain.home.service;

import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.home.dto.response.HomeProgressResponse;
import com.sopt.sopkathon_web2_server.domain.home.dto.response.HomeResponse;
import com.sopt.sopkathon_web2_server.domain.home.dto.response.TodayQuestionResponse;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
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
public class HomeService {

    private static final String STATUS_MESSAGE = "답장을 받지 못해 멀어지는 중이에요..";
    private static final String PARENT_ANSWER_PENDING_STATUS_MESSAGE = "부모님 답변은 아직이에요";
    private static final String PARENT_ANSWER_COMPLETED_STATUS_MESSAGE = "부모님을 답변을 남겼어요";
    private static final int CURRENT_STEP = 1;
    private static final int TOTAL_STEP = 4;
    private static final String STEP_ONE_MESSAGE = "아직은 머뭇거리는 중이에요.";
    private static final String STEP_TWO_MESSAGE = "한 걸음씩 다가가는 중이에요.";
    private static final String STEP_THREE_MESSAGE = "거리가 좁혀지고 있어요!";
    private static final String STEP_FOUR_MESSAGE = "드디어 맞닿았어요!";

    private final ParticipantRepository participantRepository;
    private final RoomQuestionRepository roomQuestionRepository;
    private final AnswerRepository answerRepository;

    public HomeService(
            ParticipantRepository participantRepository,
            RoomQuestionRepository roomQuestionRepository,
            AnswerRepository answerRepository
    ) {
        this.participantRepository = participantRepository;
        this.roomQuestionRepository = roomQuestionRepository;
        this.answerRepository = answerRepository;
    }

    public HomeResponse getHome(String browserToken) {
        Participant participant = findParticipant(browserToken);
        RoomQuestion roomQuestion = roomQuestionRepository
                .findFirstByRoomIdOrderByOpenedAtDesc(participant.getRoom().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
        boolean answered = answerRepository.existsByRoomQuestionIdAndParticipantId(
                roomQuestion.getId(),
                participant.getId()
        );
        boolean parentAnswered = answerRepository.existsByRoomQuestionIdAndParticipantRole(
                roomQuestion.getId(),
                ParticipantRole.PARENT
        );

        return new HomeResponse(
                participant.getRole().name(),
                STATUS_MESSAGE,
                getParentAnswerStatusMessage(parentAnswered),
                createProgressResponse(CURRENT_STEP),
                new TodayQuestionResponse(
                        roomQuestion.getId(),
                        roomQuestion.getQuestion().getContent(),
                        answered
                )
        );
    }

    private Participant findParticipant(String browserToken) {
        if (!StringUtils.hasText(browserToken)) {
            throw new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT);
        }

        return participantRepository.findByBrowserTokenHash(hashToken(browserToken))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT));
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

    private HomeProgressResponse createProgressResponse(int currentStep) {
        return new HomeProgressResponse(currentStep, TOTAL_STEP, getProgressMessage(currentStep));
    }

    private String getProgressMessage(int currentStep) {
        return switch (currentStep) {
            case 1 -> STEP_ONE_MESSAGE;
            case 2 -> STEP_TWO_MESSAGE;
            case 3 -> STEP_THREE_MESSAGE;
            case 4 -> STEP_FOUR_MESSAGE;
            default -> STEP_ONE_MESSAGE;
        };
    }

    private String getParentAnswerStatusMessage(boolean parentAnswered) {
        if (parentAnswered) {
            return PARENT_ANSWER_COMPLETED_STATUS_MESSAGE;
        }

        return PARENT_ANSWER_PENDING_STATUS_MESSAGE;
    }
}
