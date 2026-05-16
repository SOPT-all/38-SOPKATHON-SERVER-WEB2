package com.sopt.sopkathon_web2_server.domain.rooms.entity;

import com.sopt.sopkathon_web2_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 초대 링크용 토큰
    @Column(name = "invite_token_hash", nullable = false, unique = true)
    private String inviteTokenHash;

    // 현재 연속 기록 일수
    @Column(name = "current_streak_day", nullable = false)
    private Integer currentStreakDay = 1;

    // 양쪽 모두 답변 완료된 시간
    @Column(name = "last_completed_at")
    private LocalDateTime lastCompletedAt;

    // 스트릭을 마지막으로 계산한 시간
    @Column(name = "last_streak_checked_at")
    private LocalDateTime lastStreakCheckedAt;

    // 생성자
    public Room(String inviteTokenHash) {
        this.inviteTokenHash = inviteTokenHash;
        this.currentStreakDay = 1;
    }

    // 마지막 답변 완료 시간 변경
    public void updateLastCompletedAt(LocalDateTime lastCompletedAt) {
        this.lastCompletedAt = lastCompletedAt;
    }

    // 스트릭 계산한 시간 기록
    public void updateLastStreakCheckedAt(LocalDateTime lastStreakCheckedAt) {
        this.lastStreakCheckedAt = lastStreakCheckedAt;
    }

    // 연속 기록 변경
    public void updateCurrentStreakDay(Integer currentStreakDay) {
        this.currentStreakDay = currentStreakDay;
    }

}
