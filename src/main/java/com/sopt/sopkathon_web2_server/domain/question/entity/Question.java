package com.sopt.sopkathon_web2_server.domain.question.entity;

import com.sopt.sopkathon_web2_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "questions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 질문 순서
    @Column(name = "sequence", nullable = false, unique = true)
    private Integer sequence;

    // 질문 내용
    @Column(name = "content", nullable = false, length = 255)
    private String content;

    // 현재 사용 중인 질문 여부
    @Column(name = "active", nullable = false)
    private Boolean active;

    // 생성자
    public Question(
            Integer sequence,
            String content,
            Boolean active

    ) {
        this.sequence = sequence;
        this.content = content;
        this.active = active;
    }

    // 활성화 여부 업데이트
    public void updateActive(Boolean active) {
        this.active = active;
    }
}
