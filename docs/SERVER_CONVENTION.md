## Branch 
```markdown
`develop` : default 브랜치, 개발 환경
`main` : 운영 환경
`feat/(이슈 번호)` : 기능 개발, 이슈 번호로 모든 기능을 관리하므로 브랜치에서는 기타 컨벤션 사용하지 않음

- 기능 개발 후 `develop`에 먼저 머지
- `develop` 환경에서 문제 없으면 `main` 머지
```

---

## Commit
**커밋 메시지만 읽어도 어떤 내용이 변경되었는지 알 수 있도록 직관적이고 명확하게 작성하기!**

```json
(이슈 번호) (컨벤션) : 한글 커밋 메시지
- (예) 21 refactor: 즐겨찾기 실패 시 응답 구조를 공통 응답 객체로 변경
```


`feat` 새로운 기능 추가 \
`refactor` 기능 흐름 및 동작에는 변경이 없으나 구조 등을 개선 \
`fix` 개발 과정에서 생긴 오류를 수정한 경우 \
`HOTFIX!` 배포된 이후 발생한 오류를 수정한 경우 \
`test` 테스트 코드 추가, 테스트 코드 리팩토링 \
`docs` 문서를 추가, 수정한 경우 \
`chore` 프로젝트 설정과 관련된 경우


---

## Service Architecture

```json
com
└── sopt
    └── sopkathon
        ├── domain
        │   ├── domain1
        │   │   ├── controller
        │   │   ├── service
        │   │   ├── entity
        │   │   ├── dto
        │   │   │   ├── request
        │   │   │   └── response
        │   │   └── repository
        │   └── ...
        │
        └── global
            ├── config
            ├── exception
            │   ├── ErrorCode
            │   ├── BusinessException
            │   └── GlobalExceptionHandler
            └── response
                ├── ApiResponse
                ├── ErrorResponse
                └── ErrorDetail
```

## ApiResponse

- 성공 응답

```
{
    "success": true,
    "data": {}, // or [] or null
    "error": null
}
```

- 실패 응답
  -  실패 응답에서의 code는 http 상태 코드가 아닌 custom code ex)40001,40002
```
{
    "success": false,
    "data": null,
    "error": {
    "code": int,
    "message": string
        }
}
```