package com.sopt.sopkathon_web2_server.domain.question.repository;

import com.sopt.sopkathon_web2_server.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
