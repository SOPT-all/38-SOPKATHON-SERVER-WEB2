package com.sopt.sopkathon_web2_server.domain.rooms.repository;

import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByInviteTokenHash(String inviteTokenHash);

    Optional<Room> findByInviteTokenHash(String inviteTokenHash);
}
