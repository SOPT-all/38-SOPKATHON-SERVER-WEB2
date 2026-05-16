# PR #25 충돌 해결 컨텍스트 노트

- PR #25는 `feat/22`에서 `develop`으로 병합되는 답변 저장 API PR이다.
- GitHub 커넥터 기준 PR은 열려 있고 `mergeable=false` 상태다.
- 로컬 작업트리는 시작 시점에 깨끗했고 현재 브랜치는 `feat/22`였다.
- `gh auth status`에서 GitHub CLI 토큰이 만료된 상태로 확인되어 PR 메타데이터 조회는 GitHub 커넥터를 사용했다.
- 충돌 해결의 성공 기준은 최신 `develop` 병합, 충돌 표식 제거, 관련 Gradle 테스트 통과, 해결 커밋과 푸시 완료다.
- `AnswerRepository` 충돌은 `existsByRoomQuestionIdAndParticipantId`와 `countByRoomQuestionId`를 모두 유지하는 방식으로 해결했다.
- `RoomQuestionRepository` 충돌은 답변 API의 `findByIdAndRoomId`와 홈 API의 `findFirstByRoomIdOrderByOpenedAtDesc`를 모두 유지하는 방식으로 해결했다.
- 첫 테스트 실행은 `ParticipantRepository`에 같은 `findByBrowserTokenHash` 선언이 두 번 남아 컴파일 단계에서 실패했다.
- 중복 선언은 자동 병합 결과로 생긴 것이며 동일 시그니처라 하나만 유지했다.
- `./gradlew test` 재실행 결과 빌드와 테스트가 통과했다.
