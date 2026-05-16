package com.sopt.sopkathon_web2_server.domain.answers.entity;

import com.sopt.sopkathon_web2_server.domain.participants.entity.Participant;
import com.sopt.sopkathon_web2_server.domain.question.entity.RoomQuestion;
import com.sopt.sopkathon_web2_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_answer_room_question_participant",
                        columnNames = {"room_question_id", "participant_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 질문에 대한 답변인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_question_id", nullable = false)
    private RoomQuestion roomQuestion;

    // 누가 답변했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    // S3 영상 파일 경로
    @Column(name = "image_key", nullable = false)
    private String imageKey;

    // 생성자
    public Answer(
            RoomQuestion roomQuestion,
            Participant participant,
            String imageKey
    ) {
        this.roomQuestion = roomQuestion;
        this.participant = participant;
        this.imageKey = imageKey;
    }

    // S3 영상 파일 경로 업데이트
    public void updateImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
