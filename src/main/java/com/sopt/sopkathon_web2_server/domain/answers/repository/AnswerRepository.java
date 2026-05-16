package com.sopt.sopkathon_web2_server.domain.answers.repository;

import com.sopt.sopkathon_web2_server.domain.answers.entity.Answer;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    boolean existsByRoomQuestionIdAndParticipantId(Long roomQuestionId, Long participantId);

    boolean existsByRoomQuestionIdAndParticipantRole(Long roomQuestionId, ParticipantRole role);

    long countByRoomQuestionId(Long roomQuestionId);

    List<Answer> findAllByRoomQuestionId(Long roomQuestionId);
}
