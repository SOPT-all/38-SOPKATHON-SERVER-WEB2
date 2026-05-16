package com.sopt.sopkathon_web2_server.domain.participants.repository;

import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    boolean existsByBrowserTokenHash(String browserTokenHash);

    long countByRoomId(Long roomId);

    Optional<Participant> findByRoomIdAndBrowserTokenHash(Long roomId, String browserTokenHash);

    List<Participant> findAllByRoomIdOrderByParticipantOrderAsc(Long roomId);
}
