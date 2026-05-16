package com.sopt.sopkathon_web2_server.domain.question.repository;

import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomQuestionRepository extends JpaRepository<RoomQuestion, Long> {

    Optional<RoomQuestion> findFirstByRoomIdOrderByOpenedAtDesc(Long roomId);
}
