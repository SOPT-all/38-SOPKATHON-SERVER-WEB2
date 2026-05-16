package com.sopt.sopkathon_web2_server.domain.answers.repository;

import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    long countByRoomQuestionId(Long roomQuestionId);
}
