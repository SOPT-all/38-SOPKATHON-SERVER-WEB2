package com.sopt.sopkathon_web2_server.domain.records.controller;

import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.Question;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.QuestionRepository;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.JoinRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void getRecordsReturnsCompletedRecords() throws Exception {
        RoomParticipants roomParticipants = createRoomParticipants();
        RoomQuestion roomQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                1,
                "엄마가 좋아하는 음식이 뭐야?",
                LocalDateTime.of(2026, 5, 17, 20, 24, 0)
        );
        answerBothParticipants(roomQuestion, roomParticipants);

        mockMvc.perform(get("/api/records")
                        .header("Authorization", "Bearer " + roomParticipants.creatorToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.records[0].roomQuestionId").value(roomQuestion.getId()))
                .andExpect(jsonPath("$.data.records[0].question").value("엄마가 좋아하는 음식이 뭐야?"))
                .andExpect(jsonPath("$.data.records[0].completedAt").value("2026-05-17T20:24:00"))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getRecordsRejectsInvalidBrowserToken() throws Exception {
        mockMvc.perform(get("/api/records")
                        .header("Authorization", "Bearer invalid-browser-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40004))
                .andExpect(jsonPath("$.error.message").value("방 참여자 정보가 올바르지 않습니다."));
    }

    @Test
    void getRecordDetailReturnsQuestionAndAnswerVideos() throws Exception {
        RoomParticipants roomParticipants = createRoomParticipants();
        RoomQuestion roomQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                1,
                "어릴 때 꿈이 뭐였어?",
                LocalDateTime.of(2026, 5, 17, 20, 24, 0)
        );
        answerRepository.save(new Answer(roomQuestion, roomParticipants.creator(), "uploads/child-answer.mp4"));
        answerRepository.save(new Answer(roomQuestion, roomParticipants.guest(), "uploads/parent-answer.mp4"));

        mockMvc.perform(get("/api/records/{roomQuestionId}", roomQuestion.getId())
                        .header("Authorization", "Bearer " + roomParticipants.creatorToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomQuestionId").value(roomQuestion.getId()))
                .andExpect(jsonPath("$.data.question").value("어릴 때 꿈이 뭐였어?"))
                .andExpect(jsonPath("$.data.completedAt").value("2026-05-17T20:24:00"))
                .andExpect(jsonPath("$.data.answers.length()").value(2))
                .andExpect(jsonPath("$.data.answers[0].role").value("PARENT"))
                .andExpect(jsonPath("$.data.answers[0].videoUrl").value("https://test-bucket.s3.ap-northeast-2.amazonaws.com/uploads/parent-answer.mp4"))
                .andExpect(jsonPath("$.data.answers[0].answeredAt").exists())
                .andExpect(jsonPath("$.data.answers[0].isMine").value(false))
                .andExpect(jsonPath("$.data.answers[1].role").value("CHILD"))
                .andExpect(jsonPath("$.data.answers[1].videoUrl").value("https://test-bucket.s3.ap-northeast-2.amazonaws.com/uploads/child-answer.mp4"))
                .andExpect(jsonPath("$.data.answers[1].answeredAt").exists())
                .andExpect(jsonPath("$.data.answers[1].isMine").value(true))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getRecordDetailReturnsNotFoundWhenRecordDoesNotExist() throws Exception {
        RoomParticipants roomParticipants = createRoomParticipants();

        mockMvc.perform(get("/api/records/{roomQuestionId}", 999999L)
                        .header("Authorization", "Bearer " + roomParticipants.creatorToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40402))
                .andExpect(jsonPath("$.error.message").value("기록을 찾을 수 없습니다"));
    }

    @Test
    void getRecordDetailReturnsNotFoundWhenBothParticipantsHaveNotAnswered() throws Exception {
        RoomParticipants roomParticipants = createRoomParticipants();
        RoomQuestion roomQuestion = saveCompletedRoomQuestion(
                roomParticipants.room(),
                1,
                "어릴 때 꿈이 뭐였어?",
                LocalDateTime.of(2026, 5, 17, 20, 24, 0)
        );
        answerRepository.save(new Answer(roomQuestion, roomParticipants.creator(), "uploads/child-answer.mp4"));

        mockMvc.perform(get("/api/records/{roomQuestionId}", roomQuestion.getId())
                        .header("Authorization", "Bearer " + roomParticipants.creatorToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40402))
                .andExpect(jsonPath("$.error.message").value("기록을 찾을 수 없습니다"));
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
