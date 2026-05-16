package com.sopt.sopkathon_web2_server.domain.home.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

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
    void getHomeReturnsHomeResponse() throws Exception {
        CreateRoomResponse createdRoom = roomService.createRoom();
        Participant participant = participantRepository.findById(createdRoom.participantId()).orElseThrow();
        RoomQuestion roomQuestion = saveRoomQuestion(participant.getRoom(), "가장 행복했던 순간은 언제인가요?");

        mockMvc.perform(get("/api/home")
                        .header("Authorization", "Bearer " + createdRoom.browserToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.selectedMode").value("CHILD"))
                .andExpect(jsonPath("$.data.statusMessage").value("답장을 받지 못해 멀어지는 중이에요.."))
                .andExpect(jsonPath("$.data.parentAnswerStatusMessage").value("부모님 답변은 아직이에요"))
                .andExpect(jsonPath("$.data.progress.currentStep").value(1))
                .andExpect(jsonPath("$.data.progress.totalStep").value(4))
                .andExpect(jsonPath("$.data.progress.message").value("아직은 머뭇거리는 중이에요."))
                .andExpect(jsonPath("$.data.todayQuestion.roomQuestionId").value(roomQuestion.getId()))
                .andExpect(jsonPath("$.data.todayQuestion.content").value("가장 행복했던 순간은 언제인가요?"))
                .andExpect(jsonPath("$.data.todayQuestion.answered").value(false))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getHomeRejectsInvalidBrowserToken() throws Exception {
        mockMvc.perform(get("/api/home")
                        .header("Authorization", "Bearer invalid-browser-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40004))
                .andExpect(jsonPath("$.error.message").value("방 참여자 정보가 올바르지 않습니다."));
    }

    @Test
    void getHomeReturnsAnsweredTrueWhenAnswerExists() throws Exception {
        CreateRoomResponse createdRoom = roomService.createRoom();
        Participant participant = participantRepository.findById(createdRoom.participantId()).orElseThrow();
        RoomQuestion roomQuestion = saveRoomQuestion(participant.getRoom(), "오늘 나누고 싶은 말은 무엇인가요?");
        answerRepository.save(new Answer(roomQuestion, participant, "answers/test-image.png"));

        mockMvc.perform(get("/api/home")
                        .header("Authorization", "Bearer " + createdRoom.browserToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.todayQuestion.answered").value(true));
    }

    @Test
    void getHomeReturnsParentAnsweredStatusMessageWhenParentAnswerExists() throws Exception {
        CreateRoomResponse createdRoom = roomService.createRoom();
        JoinRoomResponse joinedRoom = roomService.joinRoom(createdRoom.inviteToken());
        Participant child = participantRepository.findById(createdRoom.participantId()).orElseThrow();
        Participant parent = participantRepository.findById(joinedRoom.participantId()).orElseThrow();
        RoomQuestion roomQuestion = saveRoomQuestion(child.getRoom(), "부모님과 가장 가고 싶은 곳은 어디인가요?");
        answerRepository.save(new Answer(roomQuestion, parent, "answers/parent-answer.mp4"));

        mockMvc.perform(get("/api/home")
                        .header("Authorization", "Bearer " + createdRoom.browserToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.statusMessage").value("답장을 받지 못해 멀어지는 중이에요.."))
                .andExpect(jsonPath("$.data.parentAnswerStatusMessage").value("부모님을 답변을 남겼어요"))
                .andExpect(jsonPath("$.data.progress.message").value("아직은 머뭇거리는 중이에요."));
    }

    private RoomQuestion saveRoomQuestion(Room room, String content) {
        int sequence = Math.toIntExact(questionRepository.count()) + 1;
        Question question = questionRepository.save(new Question(sequence, content, true));

        return roomQuestionRepository.save(new RoomQuestion(room, question, 1));
    }
}
