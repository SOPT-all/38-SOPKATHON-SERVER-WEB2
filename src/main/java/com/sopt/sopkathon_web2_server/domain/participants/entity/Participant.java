package com.sopt.sopkathon_web2_server.domain.participants.entity;

import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import com.sopt.sopkathon_web2_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseEntity {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 방 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // 참여 순서
    @Column(name = "participant_order", nullable = false)
    private Integer participantOrder;

    // 브라우저 토큰
    @Column(name = "browser_token_hash", nullable = false, unique = true)
    private String browserTokenHash;

    // 참여 시간
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    // 마지막으로 접속한 시간
    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    // 생성자
    public Participant(
            Room room,
            Integer participantOrder,
            String browserTokenHash
    ) {
        this.room = room;
        this.participantOrder = participantOrder;
        this.browserTokenHash = browserTokenHash;
        this.joinedAt = LocalDateTime.now();
    }

    // 마지막으로 본 시간 업데이트
    public void updateLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
