package com.sopt.sopkathon_web2_server.domain.question.entity;

import com.sopt.sopkathon_web2_server.domain.rooms.entity.Room;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "room_questions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomQuestion {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 방 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // 질문 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 방 안에서 몇 번째 질문인지
    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    // 질문이 열린 시간
    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    // 양쪽 모두 완료된 시간
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 생성자
    public RoomQuestion(
            Room room,
            Question question,
            Integer questionOrder
    ) {

        this.room = room;
        this.question = question;
        this.questionOrder = questionOrder;
        this.openedAt = LocalDateTime.now();
    }

    // 완료 시간 업데이트
    public void updateCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
