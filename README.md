# Team Collaboration Tool - 통합 프로젝트

##  프로젝트 개요
프로젝트 협업의 민족의 백엔드 코드입니다

##  기여자별 구현 기능

###  손승우 (기본 구조 및 인증)
- **인증/보안**
  - JWT 기반 인증 시스템
  - Spring Security 설정 (Swagger 포함)
  - BCrypt 비밀번호 암호화
  
- **사용자 관리**
  - 회원가입/로그인
  - 사용자 정보 조회/수정
  - 비밀번호 변경
  - 회원 탈퇴 (소프트 삭제)

###  전준환 (게시글/투표 시스템)
- **게시글 기능**
  - 게시글 CRUD
  - 파일 첨부 (FileStore)
  
- **투표 시스템**
  - 일반 투표 (Vote, VoteOption, VoteRecord)
  - 시간 투표 (TimePoll, TimeResponse)
  
- **파일 관리**
  - 파일 업로드/다운로드
  - 파일 저장소 관리

###  강재호 (프로젝트/일정 관리)
- **프로젝트 관리**
  - 프로젝트 CRUD
  - 프로젝트 멤버 관리
  
- **캘린더/일정**
  - 캘린더 이벤트 관리
  - 일정 CRUD
  
- **공지사항**
  - 공지사항 CRUD
  
- **대시보드**
  - 프로젝트 대시보드
  

##  디렉토리 구조

```
merged/
├── src/main/java/me/seungwoo/
│   ├── config/
│   │   ├── jwt/              # JWT 인증 (승우)
│   │   ├── exception/        # 예외 처리 (재호)
│   │   └── SecurityConfig    # 보안 설정 (승우)
│   │
│   ├── controller/
│   │   ├── user/             # 사용자 (승우)
│   │   ├── post/             # 게시글 (준환)
│   │   ├── vote/             # 투표 (준환)
│   │   ├── timepoll/         # 시간투표 (준환)
│   │   ├── project/          # 프로젝트 (재호)
│   │   ├── calendar/         # 캘린더 (재호)
│   │   ├── notice/           # 공지사항 (재호)
│   │   └── dashboard/        # 대시보드 (재호)
│   │
│   ├── domain/
│   │   ├── user/             # 사용자 엔티티 (승우)
│   │   ├── post/             # 게시글 엔티티 (준환)
│   │   ├── vote/             # 투표 엔티티 (준환)
│   │   ├── timepoll/         # 시간투표 엔티티 (준환)
│   │   ├── project/          # 프로젝트 엔티티 (재호)
│   │   ├── calendar/         # 캘린더 엔티티 (재호)
│   │   ├── notice/           # 공지 엔티티 (재호)
│   │   └── ...               # 기타 공통 엔티티
│   │
│   ├── dto/                  # 각 기능별 DTO
│   ├── service/              # 비즈니스 로직
│   ├── repository/           # 데이터 접근 계층
│   └── file/                 # 파일 관리 (준환)
│
└── src/main/resources/
    └── application.yml       # 설정 파일

```

## 기술 스택
- **Framework**: Spring Boot
- **Security**: Spring Security + JWT
- **Database**: H2 (개발), MySQL (프로덕션 준비)
- **ORM**: Spring Data JPA
- **Build**: Gradle
- **Java Version**: 17+

## 실행 방법

### 1. 프로젝트 클론 또는 이동
```bash
cd merged
```

### 2. 빌드
```bash
./gradlew clean build
```

### 3. 실행
```bash
./gradlew bootRun
```

### 4. H2 콘솔 접속
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (비워두기)
```

## 주요 API 엔드포인트

### 인증 (승우)
- `POST /api/users/signup` - 회원가입
- `POST /api/users/login` - 로그인
- `GET /api/users/me` - 내 정보 조회
- `PATCH /api/users/update` - 프로필 수정
- `PATCH /api/users/update/password` - 비밀번호 변경
- `DELETE /api/users/delete` - 회원 탈퇴

### 게시글 (준환)
- `POST /api/posts` - 게시글 작성
- `GET /api/posts` - 게시글 목록
- `GET /api/posts/{id}` - 게시글 조회
- `PATCH /api/posts/{id}` - 게시글 수정
- `DELETE /api/posts/{id}` - 게시글 삭제

### 투표 (준환)
- `POST /api/votes` - 투표 생성
- `POST /api/timepoll` - 시간투표 생성

### 프로젝트 (재호)
- `POST /api/projects` - 프로젝트 생성
- `GET /api/projects` - 프로젝트 목록

### 캘린더 (재호)
- `POST /api/calendar` - 일정 생성
- `GET /api/calendar` - 일정 조회

### 공지사항 (재호)
- `POST /api/notices` - 공지 작성
- `GET /api/notices` - 공지 목록

## 주의사항

1. **파일 업로드 경로 설정** (준환 기능)
   - `application.yml`에서 `file.dir` 경로를 로컬 환경에 맞게 수정
   - Windows: `C:/upload/`
   - Mac/Linux: `/Users/username/upload/`

2. **데이터베이스 설정**
   - 현재 H2 인메모리 DB 사용
   - MySQL로 변경 시 `application.yml` 수정 필요

3. **JWT 시크릿 키**
   - 프로덕션 환경에서는 반드시 환경변수로 관리

##  향후 개선 사항

- [ ] 통합 테스트 코드 작성
- [ ] API 문서화 (Swagger/Spring REST Docs)
- [ ] 에러 응답 표준화
- [ ] 로깅 시스템 구축
- [ ] CI/CD 파이프라인 구축
- [ ] Docker 컨테이너화

##  기여자
- **손승우**: 기본 구조, 인증/보안, 사용자 관리
- **전준환**: 게시글, 투표 시스템, 파일 관리
- **강재호**: 프로젝트, 캘린더, 공지사항, 대시보드
