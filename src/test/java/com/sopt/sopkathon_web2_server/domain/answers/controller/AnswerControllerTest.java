package com.sopt.sopkathon_web2_server.domain.answers.controller;

import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.Question;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.domain.rooms.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RoomQuestionRepository roomQuestionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createAnswerReturnsCreatedWrappedResponse() throws Exception {
        Room room = roomRepository.save(new Room("invite-token-hash"));
        room.updateCurrentStreakDay(6);
        participantRepository.save(new Participant(
                room,
                1,
                hashToken("valid-browser-token"),
                ParticipantRole.CHILD
        ));
        Question question = saveQuestion(1, "오늘의 질문");
        RoomQuestion roomQuestion = roomQuestionRepository.save(new RoomQuestion(room, question, 1));

        mockMvc.perform(post("/api/answers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-browser-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roomQuestionId": %d,
                                  "imageKey": "uploads/answer.jpg"
                                }
                                """.formatted(roomQuestion.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.answerId").isNumber())
                .andExpect(jsonPath("$.data.roomQuestionId").value(roomQuestion.getId()))
                .andExpect(jsonPath("$.data.imageKey").value("uploads/answer.jpg"))
                .andExpect(jsonPath("$.data.isUnlocked").value(false))
                .andExpect(jsonPath("$.data.currentStreakDay").value(6))
                .andExpect(jsonPath("$.data.currentStreakMessage").value("드디어 맞닿았어요!"))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void createAnswerRejectsMissingAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roomQuestionId": 1,
                                  "imageKey": "uploads/answer.jpg"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40003))
                .andExpect(jsonPath("$.error.message").value("브라우저 토큰이 올바르지 않습니다."));
    }

    private Question saveQuestion(int sequence, String content) {
        Question question = new Question(sequence, content, true);
        entityManager.persist(question);

        return question;
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
