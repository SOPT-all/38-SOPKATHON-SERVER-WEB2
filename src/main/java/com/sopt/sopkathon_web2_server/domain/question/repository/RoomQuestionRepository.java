package com.sopt.sopkathon_web2_server.domain.question.repository;

import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomQuestionRepository extends JpaRepository<RoomQuestion, Long> {

    List<RoomQuestion> findAllByRoomIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(Long roomId);
}
