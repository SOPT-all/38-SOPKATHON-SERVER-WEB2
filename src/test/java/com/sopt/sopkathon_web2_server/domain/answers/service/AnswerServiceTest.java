package com.sopt.sopkathon_web2_server.domain.answers.service;

import com.sopt.sopkathon_web2_server.domain.answers.dto.request.CreateAnswerRequest;
import com.sopt.sopkathon_web2_server.domain.answers.dto.response.CreateAnswerResponse;
import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.Question;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.domain.rooms.repository.RoomRepository;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AnswerServiceTest {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RoomQuestionRepository roomQuestionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createAnswerSavesVideoKeyForParticipantRoomQuestion() {
        Room room = roomRepository.save(new Room("invite-token-hash"));
        room.updateCurrentStreakDay(6);
        Participant participant = participantRepository.save(new Participant(
                room,
                1,
                hashToken("valid-browser-token"),
                ParticipantRole.CHILD
        ));
        RoomQuestion roomQuestion = roomQuestionRepository.save(new RoomQuestion(
                room,
                saveQuestion(1, "오늘의 질문"),
                1
        ));

        CreateAnswerResponse response = answerService.createAnswer(
                new CreateAnswerRequest(roomQuestion.getId(), "uploads/answer.mp4"),
                "Bearer valid-browser-token"
        );

        assertThat(response.answerId()).isNotNull();
        assertThat(response.roomQuestionId()).isEqualTo(roomQuestion.getId());
        assertThat(response.videoKey()).isEqualTo("uploads/answer.mp4");
        assertThat(response.isUnlocked()).isFalse();
        assertThat(response.currentStreakDay()).isEqualTo(6);
        assertThat(response.currentStreakMessage()).isEqualTo("드디어 맞닿았어요!");
        assertThat(response.createdAt()).isNotNull();
        assertThat(answerRepository.findById(response.answerId()).orElseThrow().getParticipant().getId())
                .isEqualTo(participant.getId());
        assertThat(answerRepository.findById(response.answerId()).orElseThrow().getVideoKey())
                .isEqualTo("uploads/answer.mp4");
    }

    @ParameterizedTest
    @CsvSource({
            "1, 아직은 머뭇거리는 중이에요.",
            "2, 한 걸음씩 다가가는 중이에요.",
            "3, 거리가 좁혀지고 있어요!",
            "4, 드디어 맞닿았어요!"
    })
    void createAnswerReturnsCurrentStreakMessage(int currentStreakDay, String currentStreakMessage) {
        Room room = roomRepository.save(new Room("invite-token-hash-" + currentStreakDay));
        room.updateCurrentStreakDay(currentStreakDay);
        participantRepository.save(new Participant(
                room,
                1,
                hashToken("browser-token-" + currentStreakDay),
                ParticipantRole.CHILD
        ));
        RoomQuestion roomQuestion = roomQuestionRepository.save(new RoomQuestion(
                room,
                saveQuestion(currentStreakDay, "오늘의 질문 " + currentStreakDay),
                1
        ));

        CreateAnswerResponse response = answerService.createAnswer(
                new CreateAnswerRequest(roomQuestion.getId(), "uploads/answer-" + currentStreakDay + ".mp4"),
                "Bearer browser-token-" + currentStreakDay
        );

        assertThat(response.currentStreakMessage()).isEqualTo(currentStreakMessage);
    }

    @Test
    void createAnswerUnlocksWhenBothParticipantsAnswerSameQuestion() {
        Room room = roomRepository.save(new Room("invite-token-hash"));
        Participant firstParticipant = participantRepository.save(new Participant(
                room,
                1,
                hashToken("first-browser-token"),
                ParticipantRole.CHILD
        ));
        participantRepository.save(new Participant(
                room,
                2,
                hashToken("second-browser-token"),
                ParticipantRole.PARENT
        ));
        RoomQuestion roomQuestion = roomQuestionRepository.save(new RoomQuestion(
                room,
                saveQuestion(1, "오늘의 질문"),
                1
        ));
        answerService.createAnswer(
                new CreateAnswerRequest(roomQuestion.getId(), "uploads/first.mp4"),
                "Bearer first-browser-token"
        );

        CreateAnswerResponse response = answerService.createAnswer(
                new CreateAnswerRequest(roomQuestion.getId(), "uploads/second.mp4"),
                "Bearer second-browser-token"
        );

        RoomQuestion updatedRoomQuestion = roomQuestionRepository.findById(roomQuestion.getId()).orElseThrow();
        Room updatedRoom = roomRepository.findById(room.getId()).orElseThrow();
        assertThat(response.isUnlocked()).isTrue();
        assertThat(updatedRoomQuestion.getCompletedAt()).isNotNull();
        assertThat(updatedRoom.getLastCompletedAt()).isNotNull();
        assertThat(answerRepository.existsByRoomQuestionIdAndParticipantId(roomQuestion.getId(), firstParticipant.getId()))
                .isTrue();
    }

    @Test
    void createAnswerRejectsInvalidBrowserToken() {
        CreateAnswerRequest request = new CreateAnswerRequest(1L, "uploads/answer.mp4");

        assertThatThrownBy(() -> answerService.createAnswer(request, "Bearer unknown-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_BROWSER_TOKEN);
    }

    @Test
    void createAnswerRejectsMissingRoomQuestion() {
        Room room = roomRepository.save(new Room("invite-token-hash"));
        participantRepository.save(new Participant(
                room,
                1,
                hashToken("valid-browser-token"),
                ParticipantRole.CHILD
        ));

        assertThatThrownBy(() -> answerService.createAnswer(
                new CreateAnswerRequest(999L, "uploads/answer.jpg"),
                "Bearer valid-browser-token"
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.QUESTION_NOT_FOUND);
    }

    @Test
    void createAnswerRejectsRoomQuestionFromOtherRoom() {
        Room participantRoom = roomRepository.save(new Room("participant-room-token-hash"));
        Room otherRoom = roomRepository.save(new Room("other-room-token-hash"));
        participantRepository.save(new Participant(
                participantRoom,
                1,
                hashToken("valid-browser-token"),
                ParticipantRole.CHILD
        ));
        RoomQuestion otherRoomQuestion = roomQuestionRepository.save(new RoomQuestion(
                otherRoom,
                saveQuestion(1, "다른 방 질문"),
                1
        ));

        assertThatThrownBy(() -> answerService.createAnswer(
                new CreateAnswerRequest(otherRoomQuestion.getId(), "uploads/answer.jpg"),
                "Bearer valid-browser-token"
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.QUESTION_NOT_FOUND);
    }

    @Test
    void createAnswerRejectsAlreadyAnsweredQuestion() {
        Room room = roomRepository.save(new Room("invite-token-hash"));
        participantRepository.save(new Participant(
                room,
                1,
                hashToken("valid-browser-token"),
                ParticipantRole.CHILD
        ));
        RoomQuestion roomQuestion = roomQuestionRepository.save(new RoomQuestion(
                room,
                saveQuestion(1, "오늘의 질문"),
                1
        ));
        answerService.createAnswer(
                new CreateAnswerRequest(roomQuestion.getId(), "uploads/first.mp4"),
                "Bearer valid-browser-token"
        );

        assertThatThrownBy(() -> answerService.createAnswer(
                new CreateAnswerRequest(roomQuestion.getId(), "uploads/second.mp4"),
                "Bearer valid-browser-token"
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ANSWER_ALREADY_EXISTS);
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

    private Question saveQuestion(int sequence, String content) {
        Question question = new Question(sequence, content, true);
        entityManager.persist(question);

        return question;
    }
}
