package com.sopt.sopkathon_web2_server.domain.home.service;

import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import com.sopt.sopkathon_web2_server.domain.answers.repository.AnswerRepository;
import com.sopt.sopkathon_web2_server.domain.home.dto.response.HomeResponse;
import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.participants.repository.ParticipantRepository;
import com.sopt.sopkathon_web2_server.domain.question.entity.Question;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.domain.question.repository.QuestionRepository;
import com.sopt.sopkathon_web2_server.domain.question.repository.RoomQuestionRepository;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class HomeServiceTest {

    @Autowired
    private HomeService homeService;

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
    void getHomeReturnsCurrentQuestionAndUnansweredStatus() {
        CreateRoomResponse createdRoom = roomService.createRoom();
        Participant participant = participantRepository.findById(createdRoom.participantId()).orElseThrow();
        RoomQuestion roomQuestion = saveRoomQuestion(participant.getRoom(), "가장 행복했던 순간은 언제인가요?");

        HomeResponse response = homeService.getHome(createdRoom.browserToken());

        assertThat(response.selectedMode()).isEqualTo("CHILD");
        assertThat(response.statusMessage()).isEqualTo("답장을 받지 못해 멀어지는 중이에요..");
        assertThat(response.progress().currentStep()).isEqualTo(1);
        assertThat(response.progress().totalStep()).isEqualTo(4);
        assertThat(response.progress().message()).isEqualTo("아직은 머뭇거리는 중이에요.");
        assertThat(response.todayQuestion().roomQuestionId()).isEqualTo(roomQuestion.getId());
        assertThat(response.todayQuestion().content()).isEqualTo("가장 행복했던 순간은 언제인가요?");
        assertThat(response.todayQuestion().answered()).isFalse();
    }

    @Test
    void getHomeRejectsInvalidBrowserToken() {
        assertThatThrownBy(() -> homeService.getHome("invalid-browser-token"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_ROOM_PARTICIPANT);
    }

    @Test
    void getHomeReturnsAnsweredStatusWhenAnswerExists() {
        CreateRoomResponse createdRoom = roomService.createRoom();
        Participant participant = participantRepository.findById(createdRoom.participantId()).orElseThrow();
        RoomQuestion roomQuestion = saveRoomQuestion(participant.getRoom(), "오늘 나누고 싶은 말은 무엇인가요?");
        answerRepository.save(new Answer(roomQuestion, participant, "answers/test-image.png"));

        HomeResponse response = homeService.getHome(createdRoom.browserToken());

        assertThat(response.todayQuestion().answered()).isTrue();
    }

    private RoomQuestion saveRoomQuestion(Room room, String content) {
        int sequence = Math.toIntExact(questionRepository.count()) + 1;
        Question question = questionRepository.save(new Question(sequence, content, true));

        return roomQuestionRepository.save(new RoomQuestion(room, question, 1));
    }
}
