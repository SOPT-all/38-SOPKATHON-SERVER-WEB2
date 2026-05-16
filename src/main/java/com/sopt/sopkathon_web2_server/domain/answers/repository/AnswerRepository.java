package com.sopt.sopkathon_web2_server.domain.answers.repository;

import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    boolean existsByRoomQuestionIdAndParticipantId(Long roomQuestionId, Long participantId);

    long countByRoomQuestionId(Long roomQuestionId);

    List<Answer> findAllByRoomQuestionId(Long roomQuestionId);
}
