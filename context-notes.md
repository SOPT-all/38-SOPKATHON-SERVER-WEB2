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
- 병합 해결 커밋 `1a32270`을 원격 `feat/24`에 푸시했다.

# 기록 상세 조회 API 컨텍스트 노트

- 요청 의도는 질문-답변 기록 목록에서 선택한 완료 기록의 질문 정보와 부모님/나의 답변 영상을 조회하는 API를 추가하는 것이다.
- 작업은 최신 `origin/develop`을 fast-forward 한 뒤 `codex/record-detail-api` 브랜치에서 진행한다.
- 기존 목록 API가 `/api/records`를 사용하므로 상세 API는 같은 컨트롤러에 `GET /api/records/{roomQuestionId}`로 추가한다.
- 브라우저 토큰 처리와 참여자 식별은 기존 `RecordsService.getRecords` 흐름을 재사용하고, 같은 방에 속하지 않거나 양쪽 답변이 완료되지 않은 질문은 기록으로 보지 않는다.
- 새 에러 코드는 명세의 `40402`와 메시지 `기록을 찾을 수 없습니다`를 그대로 사용한다.
- `Answer`에는 S3 객체 키만 저장되어 있으므로 응답의 `videoUrl`은 `aws.s3.bucket`과 `aws.region` 설정으로 공개 S3 URL을 조립한다.
- 상세 조회 컨트롤러 테스트 3개를 먼저 추가했고, 구현 전 `./gradlew test --tests com.sopt.sopkathon_web2_server.domain.records.controller.RecordsControllerTest`에서 상세 조회 케이스 3개가 실패했다.
- 상세 조회 구현 후 같은 컨트롤러 테스트 명령이 통과했다.
- 기록 도메인 관련 테스트 `./gradlew test --tests '*Records*'`가 통과했다.
- 전체 테스트 `./gradlew test`가 통과했다.

# 부모님 답변 상태 메시지 컨텍스트 노트

- 요청 의도는 홈 응답에서 부모님이 오늘 질문에 답변했는지에 따라 안내 문구를 다르게 내려주는 것이다.
- 기존 홈 응답에는 `statusMessage` 문자열이 이미 있으므로 새 응답 필드를 추가하지 않고 이 값을 부모님 답변 여부에 따라 반환하는 방식으로 진행한다.
- 부모님 답변 여부는 현재 사용자의 답변 여부가 아니라 `ParticipantRole.PARENT` 역할 참여자의 답변 존재 여부로 판단한다.
- 부모님이 아직 입장하지 않았거나 답변이 없으면 부모님 답변이 없는 상태로 본다.
- 성공 기준은 미답변일 때 `부모님 답변은 아직이에요`, 답변 완료일 때 `부모님을 답변을 남겼어요`가 반환되고 관련 홈 테스트와 전체 테스트가 통과하는 것이다.
- 서비스와 컨트롤러 테스트를 먼저 변경했고 `./gradlew test --tests '*Home*'`에서 기존 정적 `statusMessage`가 반환되어 4개 테스트가 실패하는 것을 확인했다.
- `AnswerRepository.existsByRoomQuestionIdAndParticipantRole`로 부모 역할 답변 존재 여부를 조회하고 `HomeService`가 상태 메시지를 선택하도록 구현했다.
- 구현 후 `./gradlew test --tests '*Home*'`가 통과했다.
- 전체 테스트 `./gradlew test`가 통과했다.

# 부모님 답변 상태 메시지 분리 컨텍스트 노트

- 사용자 피드백에 따라 부모님 답변 상태 문구와 단계별 진행도 문구는 별도 의미로 유지해야 한다.
- 이전 구현은 부모님 답변 상태 문구를 `HomeResponse.statusMessage`에 넣어 기존 홈 상태 문구를 대체했으므로 응답 필드 역할을 섞은 것이다.
- 수정 방향은 `statusMessage`를 기존 `답장을 받지 못해 멀어지는 중이에요..`로 복구하고, 부모님 답변 상태 문구는 새 `parentAnswerStatusMessage` 필드로 분리하는 것이다.
- `progress.message`는 기존 step1부터 step4까지의 단계 문구만 담당한다.
- 테스트를 먼저 `parentAnswerStatusMessage` 기대값으로 수정했고 새 record accessor가 없어 `./gradlew test --tests '*Home*'`가 `cannot find symbol`로 실패하는 것을 확인했다.
- `HomeResponse`에 `parentAnswerStatusMessage`를 추가하고 `HomeService`에서 기존 `statusMessage`와 분리해 내려주도록 수정했다.
- 수정 후 `./gradlew test --tests '*Home*'`가 통과했다.
- 전체 테스트 `./gradlew test`가 통과했다.

# 방 생성 시 질문 배정 컨텍스트 노트

- 요청 의도는 운영에서 홈 화면 조회가 500으로 실패하지 않도록 방 생성 시점에 해당 방의 첫 질문을 배정하는 것이다.
- 작업은 `develop` 기준 `codex/assign-question-on-room-create` 브랜치에서 진행한다.
- 작업 계획은 방 생성 직후 `RoomQuestion`이 생성되는 테스트를 먼저 추가하고, `RoomService.createRoom`에서 첫 활성 질문을 조회해 `RoomQuestion`을 저장한 뒤 홈 조회까지 관련 테스트로 검증하는 것이다.
- 성공 기준은 수동으로 `room_questions` 데이터를 넣지 않아도 `createRoom()` 직후 `homeService.getHome(browserToken)`이 오늘 질문을 반환하고 관련 테스트와 전체 테스트가 통과하는 것이다.
- 운영 로그 기준 원인은 `HomeService.getHome`이 참여자 방의 최신 `RoomQuestion`을 찾지 못해 `INTERNAL_SERVER_ERROR`를 던진 것이다.
- `RoomServiceTest.createRoomAssignsFirstActiveQuestionToRoom`을 추가했고 구현 전 `./gradlew test --tests com.sopt.sopkathon_web2_server.domain.rooms.service.RoomServiceTest.createRoomAssignsFirstActiveQuestionToRoom`는 `RoomQuestion` 조회 결과가 없어 `NoSuchElementException`으로 실패했다.
- `RoomService.createRoom`에서 첫 활성 질문을 찾아 `RoomQuestion`으로 저장하도록 구현했다.
- 질문 카탈로그가 비어 있는 테스트 환경의 기존 방 생성 테스트 흐름은 유지하기 위해 활성 질문이 있을 때만 배정한다.
- 구현 후 단일 RED 테스트, `./gradlew test --tests com.sopt.sopkathon_web2_server.domain.rooms.service.RoomServiceTest --tests '*Home*'`, `./gradlew test`가 모두 통과했다.
