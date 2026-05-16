package com.sopt.sopkathon_web2_server.domain.records.service;

import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.Question;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.QuestionRepository;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordsResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.JoinRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RecordsServiceTest {

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private RoomQuestionRepository roomQuestionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void getRecordsReturnsCompletedRecords() {
        RoomParticipants roomParticipants = createRoomParticipants();
        RoomQuestion completedQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                1,
                "엄마가 좋아하는 음식이 뭐야?",
                LocalDateTime.of(2026, 5, 17, 20, 24, 0)
        );
        answerBothParticipants(completedQuestion, roomParticipants);

        RecordsResponse response = recordsService.getRecords(roomParticipants.creatorToken());

        assertThat(response.records()).hasSize(1);
        assertThat(response.records().getFirst().roomQuestionId()).isEqualTo(completedQuestion.getId());
        assertThat(response.records().getFirst().question()).isEqualTo("엄마가 좋아하는 음식이 뭐야?");
        assertThat(response.records().getFirst().completedAt()).isEqualTo(LocalDateTime.of(2026, 5, 17, 20, 24, 0));
    }

    @Test
    void getRecordsExcludesQuestionsAnsweredByOnlyOneParticipant() {
        RoomParticipants roomParticipants = createRoomParticipants();
        RoomQuestion completedQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                1,
                "엄마가 좋아하는 음식이 뭐야?",
                LocalDateTime.of(2026, 5, 17, 20, 24, 0)
        );
        answerBothParticipants(completedQuestion, roomParticipants);

        RoomQuestion partiallyAnsweredQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                2,
                "젊었을 때 가장 후회되는 일은?",
                LocalDateTime.of(2026, 5, 17, 21, 10, 0)
        );
        answerRepository.save(new Answer(partiallyAnsweredQuestion, roomParticipants.creator(), "answers/creator-only.png"));

        RecordsResponse response = recordsService.getRecords(roomParticipants.creatorToken());

        assertThat(response.records()).hasSize(1);
        assertThat(response.records().getFirst().roomQuestionId()).isEqualTo(completedQuestion.getId());
    }

    @Test
    void getRecordsReturnsRecordsSortedByCompletedAtDesc() {
        RoomParticipants roomParticipants = createRoomParticipants();
        RoomQuestion olderQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                1,
                "엄마가 좋아하는 음식이 뭐야?",
                LocalDateTime.of(2026, 5, 17, 20, 24, 0)
        );
        answerBothParticipants(olderQuestion, roomParticipants);

        RoomQuestion newerQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                2,
                "젊었을 때 가장 후회되는 일은?",
                LocalDateTime.of(2026, 5, 17, 21, 10, 0)
        );
        answerBothParticipants(newerQuestion, roomParticipants);

        RecordsResponse response = recordsService.getRecords(roomParticipants.creatorToken());

        assertThat(response.records()).hasSize(2);
        assertThat(response.records())
                .extracting("roomQuestionId")
                .containsExactly(newerQuestion.getId(), olderQuestion.getId());
    }

    @Test
    void getRecordsRejectsInvalidBrowserToken() {
        assertThatThrownBy(() -> recordsService.getRecords("invalid-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_ROOM_PARTICIPANT);
    }

    private RoomParticipants createRoomParticipants() {
        CreateRoomResponse createdRoom = roomService.createRoom();
        JoinRoomResponse joinedRoom = roomService.joinRoom(createdRoom.inviteToken());
        Participant creator = participantRepository.findById(createdRoom.participantId()).orElseThrow();
        Participant guest = participantRepository.findById(joinedRoom.participantId()).orElseThrow();

        return new RoomParticipants(creator.getRoom(), creator, guest, createdRoom.browserToken());
    }

    private RoomQuestion saveRoomQuestion(Room room, int questionOrder, String content) {
        int sequence = Math.toIntExact(questionRepository.count()) + 1;
        Question question = questionRepository.save(new Question(sequence, content, true));

        return roomQuestionRepository.save(new RoomQuestion(room, question, questionOrder));
    }

    private RoomQuestion saveCompletedRoomQuestion(
            Room room,
            int questionOrder,
            String content,
            LocalDateTime completedAt
    ) {
        RoomQuestion roomQuestion = saveRoomQuestion(room, questionOrder, content);
        roomQuestion.updateCompletedAt(completedAt);

        return roomQuestionRepository.save(roomQuestion);
    }

    private void answerBothParticipants(RoomQuestion roomQuestion, RoomParticipants roomParticipants) {
        answerRepository.save(new Answer(roomQuestion, roomParticipants.creator(), "answers/creator.png"));
        answerRepository.save(new Answer(roomQuestion, roomParticipants.guest(), "answers/guest.png"));
    }

    private record RoomParticipants(
            Room room,
            Participant creator,
            Participant guest,
            String creatorToken
    ) {
    }
}
