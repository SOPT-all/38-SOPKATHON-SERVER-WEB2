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
- 병합 커밋 `c1e7229`를 원격 `feat/22`에 푸시했고 GitHub PR #25는 `mergeable=true`로 확인됐다.

# Swagger 어노테이션 추가 컨텍스트 노트

- 요청 의도는 Swagger UI에서 API를 바로 테스트할 수 있도록 공개 API에 설명과 예시 메타데이터를 붙이는 것이다.
- 현재 springdoc 의존성과 `SwaggerConfig`는 이미 존재하므로 새 문서 라이브러리나 설정 계층을 추가하지 않는다.
- 공개 컨트롤러 범위는 `RoomController`, `InviteController`, `AnswerController`, `UploadController`, `HomeController`다.
- 성공 기준은 `/v3/api-docs`에 각 API의 태그, 요약, 요청 본문, Authorization 헤더 설명, DTO 스키마 예시가 노출되고 `./gradlew test`가 통과하는 것이다.
- `OpenApiDocumentationTest`를 먼저 추가했고 기존 코드에서는 `$.paths['/api/rooms'].post.tags[0]` 검증에서 실패해 Swagger 태그와 요약 메타데이터 부재를 확인했다.
- 컨트롤러 변경은 springdoc 표준 어노테이션만 사용하고 런타임 요청 처리 로직은 변경하지 않았다.
- 어노테이션 추가 후 `OpenApiDocumentationTest` 단독 실행과 `./gradlew test` 전체 실행이 통과했다.

# PR #28 main 병합 해결 컨텍스트 노트

- PR #28은 `feat/24`의 질문-답변 목록 조회 PR이다.
- GitHub 커넥터 기준 PR base는 `develop`이지만, 로컬 확인 결과 `origin/develop`은 `origin/main`의 조상이고 `origin/main`에 `b1cf26a chore: 스웨거 https 적용`이 추가로 있다.
- 사용자의 기준 정정에 따라 잘못 시작한 `origin/develop` 병합은 중단했고, 최신 기준을 `origin/main`으로 바꿨다.
- `origin/main` 병합에서도 충돌은 동일하게 재현됐다.
- `AnswerRepository` 충돌은 답변 저장 API의 `existsByRoomQuestionIdAndParticipantId`와 목록 조회 API의 `countByRoomQuestionId`를 모두 유지하는 방식으로 해결했다.
- `RoomQuestionRepository` 충돌은 답변 저장 API의 `findByIdAndRoomId`, 홈 API의 `findFirstByRoomIdOrderByOpenedAtDesc`, 목록 조회 API의 `findAllByRoomIdAndCompletedAtIsNotNullOrderByCompletedAtDesc`를 모두 유지하는 방식으로 해결했다.
- `RoomController`, `InviteController`, `UploadController`와 관련 DTO의 Swagger 중복 충돌은 최신 main의 태그와 요약을 유지했다.
- 자동 병합으로 `ParticipantRepository.findByBrowserTokenHash`가 중복 선언되어 하나만 유지했다.
- 충돌 표식 검색 `rg -n '<<<<<<<|=======|>>>>>>>' .` 결과는 비어 있었다.
- `./gradlew test --tests '*Records*'`와 `./gradlew test`가 모두 통과했다.
