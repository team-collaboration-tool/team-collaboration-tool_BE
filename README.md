# HM (Hyup-Min) - 팀 협업 관리 시스템

## 프로젝트 개요
**HM(협민)**은 팀 프로젝트 협업을 위한 종합 관리 시스템입니다. 
프로젝트 관리, 게시판, 공지사항, 투표, 시간 조율, 일정 관리 등 팀 협업에 필요한 모든 기능을 제공합니다.

- **API 문서 (Swagger)**: http://hyupmin.ap-northeast-2.elasticbeanstalk.com/swagger-ui/index.html

## 팀원 및 역할

### 손승우 - 인증/보안 및 인프라
- **인증 시스템**
  - JWT 기반 토큰 인증 구현
  - Spring Security 설정 및 필터 체인 구성
  - BCrypt 비밀번호 암호화
  
- **사용자 관리**
  - 회원가입/로그인 (이메일 중복 체크, 비밀번호 확인)
  - 사용자 정보 조회/수정
  - 비밀번호 변경
  - 회원 탈퇴 (Soft Delete 방식)
  
- **프로젝트 인프라**
  - AWS Elastic Beanstalk 배포
  - MySQL RDS 데이터베이스 설정
  - CORS 설정 (프론트엔드 연동)
  - Swagger UI 통합
  - 공통 예외 처리 구조

### 전준환 - 게시판 및 투표 시스템
- **게시글 시스템**
  - 게시글 CRUD (생성, 조회, 수정, 삭제)
  - 게시글 페이징 및 정렬 (작성일, 조회수, 댓글수)
  - 검색 기능 (제목, 내용, 작성자)
  - 멀티파트 파일 첨부 및 관리
  - 첨부 파일 다운로드
  
- **공지사항 시스템**
  - 프로젝트별 공지사항 생성
  - 공지사항 목록 조회
  - 프로젝트 멤버만 접근 가능
  
- **투표 기능**
  - 일반 투표 생성 (단일/중복 선택)
  - 투표하기 및 재투표 기능
  - 투표 옵션별 결과 집계
  - 투표 참여자 관리
  
- **시간 조율 투표**
  - 날짜/시간대 그리드 기반 투표 생성
  - 개인별 선택 시간 + 팀 전체 히트맵 동시 제공
  - 드래그 방식 시간 선택
  - 시간대별 참여자 수 집계
  - 최적 시간대 시각화
  
- **파일 관리**
  - UUID 기반 파일명 생성 (충돌 방지)
  - 멀티파트 파일 업로드/다운로드
  - 게시글/투표 파일 첨부
  - 파일 크기 제한 (단일 10MB, 전체 50MB)

### 강재호 - 프로젝트 및 일정 관리
- **프로젝트 관리**
  - 프로젝트 생성 및 CRUD
  - UUID 기반 초대 코드 시스템
  - 프로젝트 멤버 관리 (OWNER/MEMBER 역할)
  - 멤버 승인/거절/추방 기능
  
- **캘린더 기능**
  - 일정 생성 및 CRUD
  - 일정 참가자 관리 (Many-to-Many)
  - 프로젝트별/전체 일정 조회
  
## 기술 스택

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA (Hibernate)
- **Database**: MySQL 8.0 (Production) / H2 (Development)
- **Build Tool**: Gradle 8.x
- **API Documentation**: Swagger UI (SpringDoc OpenAPI 2.3.0)

### Infrastructure & Deployment
- **Cloud Platform**: AWS
- **Application Server**: Elastic Beanstalk (Java 17 Corretto)
- **Database**: RDS MySQL 8.0 (db.t3.micro)
- **Instance**: t2.micro
- **Port**: 5000 (Elastic Beanstalk default)
- **Profile**: dev (local) / prod (AWS)
- **Health Check**: Spring Boot Actuator

### Key Libraries
- `spring-boot` (3.2.0) - Spring Boot 프레임워크
- `jjwt` (0.11.5) - JWT 토큰 생성/검증
- `springdoc-openapi` (2.3.0) - API 문서화 (Swagger UI)
- `spring-boot-starter-actuator` - Health Check
- `lombok` - 코드 간소화
- `spring-boot-starter-validation` - 입력 검증
- `mysql-connector-j` - MySQL 드라이버
- `h2database` - 개발용 인메모리 DB

## 프로젝트 구조

```
src/main/java/com/hyupmin/
├── config/
│   ├── jwt/
│   │   ├── JwtTokenProvider.java      # JWT 토큰 생성/검증
│   │   └── JwtAuthenticationFilter.java # JWT 인증 필터
│   ├── exception/                     # 공통 예외 처리
│   ├── SecurityConfig.java            # Spring Security 설정
│   ├── SwaggerConfig.java             # Swagger 설정
│   └── WebConfig.java                 # CORS 설정
│
├── controller/
│   ├── user/UserController.java       # 사용자 API
│   ├── post/PostController.java       # 게시글 API
│   ├── notice/NoticeController.java   # 공지사항 API
│   ├── vote/VoteController.java       # 투표 API
│   ├── timepoll/TimePollController.java # 시간투표 API
│   ├── project/ProjectController.java  # 프로젝트 API
│   └── calendar/CalendarController.java # 캘린더 API
│
├── domain/
│   ├── user/User.java                 # 사용자 엔티티
│   ├── post/Post.java                 # 게시글 엔티티
│   ├── notice/Notice.java             # 공지사항 엔티티
│   ├── vote/
│   │   ├── Vote.java                  # 투표 엔티티
│   │   └── VoteResponse.java          # 투표 응답
│   ├── voteOption/VoteOption.java     # 투표 옵션
│   ├── voteResponse/                  # 투표 응답 도메인
│   ├── timepoll/TimePoll.java         # 시간투표 엔티티
│   ├── timeResponse/TimeResponse.java # 시간투표 응답
│   ├── project/Project.java           # 프로젝트 엔티티
│   ├── projectUser/ProjectUser.java   # 프로젝트-사용자 관계
│   ├── calendar/CalendarEvent.java    # 일정 엔티티
│   ├── attachmentFile/AttachmentFile.java # 첨부파일 엔티티
│   └── BaseTimeEntity.java            # 공통 시간 필드
│
├── service/                           # 비즈니스 로직
│   ├── user/UserService.java
│   ├── post/PostService.java
│   ├── notice/NoticeService.java
│   ├── vote/VoteService.java
│   ├── timepoll/TimePollService.java
│   ├── project/ProjectService.java
│   └── calendar/CalendarService.java
│
├── repository/                        # JPA Repository
│   ├── user/UserRepository.java
│   ├── post/PostRepository.java
│   ├── notice/NoticeRepository.java
│   ├── vote/VoteRepository.java
│   ├── TimePoll/TimePollRepository.java
│   ├── project/ProjectRepository.java
│   ├── calendar/CalendarRepository.java
│   └── attachmentFile/AttachmentFileRepository.java
│
├── dto/                              # 요청/응답 DTO
│   ├── user/
│   ├── post/
│   ├── vote/
│   ├── timepoll/
│   ├── project/
│   ├── calendar/
│   └── ErrorResponse.java
│
├── file/FileStore.java               # 파일 저장소 관리
└── HyupminApplication.java           # 메인 애플리케이션
```

## 실행 방법

### 사전 요구사항
- Java 17 이상
- MySQL 8.0 (또는 H2 사용)
- Gradle 7.x 이상

### 로컬 실행

1. **프로젝트 클론**
```bash
git clone https://github.com/team-collaboration-tool/team-collaboration-tool_BE.git
cd team-collaboration-tool_BE
```

2. **환경 변수 설정**
`src/main/resources/application.yml` 파일에서 프로필 설정
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILE:dev}  # dev 또는 prod

# JWT 설정
jwt:
  secret: ${JWT_SECRET:hyupmin-dev-secret-key-change-in-production}
  expiration: 86400000  # 24시간

# 파일 업로드
file:
  dir: ${FILE_UPLOAD_DIR:/var/app/uploads/}

# 개발 환경 (H2 사용)
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb
    username: sa
    password:

# 배포 환경 (MySQL 사용)
spring:
  datasource:
    driver-class-name: ${DB_DRIVER:com.mysql.cj.jdbc.Driver}
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

3. **빌드 및 실행**
```bash
./gradlew clean build
./gradlew bootRun
```

4. **접속**
- API 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- H2 Console: http://localhost:8080/h2-console

## API 엔드포인트

### 인증 (Public - JWT 불필요)
```
POST   /api/users/signup           # 회원가입
POST   /api/users/login            # 로그인 (JWT 토큰 발급)
GET    /api/users/check-email      # 이메일 중복 확인
```

### 사용자 (JWT 필요)
```
GET    /api/users/me               # 내 정보 조회
PATCH  /api/users/update           # 프로필 수정
POST   /api/users/verify-password  # 비밀번호 확인
PATCH  /api/users/update/password  # 비밀번호 변경
DELETE /api/users/delete           # 회원 탈퇴
```

### 프로젝트
```
POST   /api/projects               # 프로젝트 생성
GET    /api/projects               # 내 프로젝트 목록
GET    /api/projects/{id}          # 프로젝트 상세
PUT    /api/projects/{id}          # 프로젝트 수정
DELETE /api/projects/{id}          # 프로젝트 삭제
POST   /api/projects/join          # 초대 코드로 참여
POST   /api/projects/{id}/approve  # 멤버 승인
POST   /api/projects/{id}/reject   # 멤버 거절
DELETE /api/projects/{id}/kick     # 멤버 추방
```

### 게시글
```
POST   /api/posts                  # 게시글 작성 (멀티파트 파일 첨부)
GET    /api/posts                  # 게시글 목록 (페이징, 검색, 정렬)
GET    /api/posts/{id}             # 게시글 상세
PUT    /api/posts/{id}             # 게시글 수정
DELETE /api/posts/{id}             # 게시글 삭제
GET    /api/posts/download/{fileId} # 첨부파일 다운로드

# 검색 파라미터
- searchType: TITLE, CONTENT, AUTHOR
- keyword: 검색어
- sort: LATEST, VIEWS, COMMENTS
```

### 공지사항
```
GET    /api/projects/{projectId}/notices  # 공지사항 목록 조회
POST   /api/projects/{projectId}/notices  # 공지사항 생성 (OWNER만 가능)
```

### 투표
```
POST   /api/votes                         # 투표 생성
POST   /api/votes/options/{optionId}/cast # 투표하기
PUT    /api/votes/options/{optionId}/cast # 재투표하기
PUT    /api/votes/{voteId}/recast         # 중복 선택 재투표
```

### 시간 조율 투표
```
GET    /api/time-poll/list/{projectId}    # 프로젝트별 시간투표 목록
POST   /api/time-poll                     # 시간투표 생성
GET    /api/time-poll/{pollId}?userId={userId} # 시간투표 상세 (개인+팀 히트맵)
POST   /api/time-poll/submit              # 시간 선택 제출
```

### 캘린더
```
GET    /api/calendar/me            # 내 전체 일정
GET    /api/calendar/projects/{projectId} # 프로젝트 일정
POST   /api/calendar/projects/{projectId} # 일정 생성
GET    /api/calendar/projects/{projectId}/events/{eventId} # 일정 상세
PUT    /api/calendar/projects/{projectId}/events/{eventId} # 일정 수정
DELETE /api/calendar/projects/{projectId}/events/{eventId} # 일정 삭제
```

## 보안

### JWT 인증
- Header: `Authorization: Bearer {token}`
- 토큰 만료 시간: 24시간 (86400000ms)
- 알고리즘: HS256

### 비밀번호 암호화
- BCrypt 해싱 알고리즘 사용
- Salt 자동 생성

### CORS 설정
- 허용 Origin:
  - `http://localhost:3000` (React 개발)
  - `http://localhost:5173` (Vite 개발)
  - `http://3.22.89.177` (프론트엔드 EC2)
- 허용 메서드: GET, POST, PUT, DELETE, PATCH, OPTIONS
- 인증 정보 포함: true

## 데이터베이스 ERD 주요 관계

```
User (1) ─────< (N) ProjectUser (N) >───── (1) Project
  │                                            │
  │                                            │
  ├─< Post >─── AttachmentFile                │
  │                                            │
  ├─< Notice (공지사항)                         │
  │                                            │
  ├─< Vote >─── VoteOption >─── VoteResponse  │
  │                                            │
  ├─< TimePoll >─── TimeResponse              │
  │                                            │
  └─< CalendarEvent (N) >─────< (N) User (참가자)
```

### 주요 엔티티 설명
- **User**: 사용자 정보 (이메일, 비밀번호, 프로필)
- **Project**: 프로젝트 정보 (이름, 설명, 초대 코드)
- **ProjectUser**: 프로젝트-사용자 관계 (역할: OWNER/MEMBER)
- **Post**: 게시글 (제목, 내용, 조회수, 댓글수)
- **Notice**: 공지사항 (프로젝트별, OWNER만 생성 가능)
- **Vote**: 투표 (단일/중복 선택)
- **VoteOption**: 투표 선택지
- **VoteResponse**: 사용자 투표 응답
- **TimePoll**: 시간 조율 투표 (날짜/시간 그리드)
- **TimeResponse**: 시간대별 사용자 응답
- **CalendarEvent**: 일정 (시작/종료 시간, 참가자)
- **AttachmentFile**: 첨부파일 (UUID 기반 파일명)

## 배포 정보

### AWS Elastic Beanstalk
- **플랫폼**: Java 17 (Corretto)
- **인스턴스**: t2.micro
- **리전**: ap-northeast-2 (Seoul)
- **포트**: 5000 (Elastic Beanstalk 기본 포트)
- **환경 변수**:
  - `SPRING_PROFILE`: prod
  - `JWT_SECRET`: JWT 비밀키 (256비트 이상)
  - `DB_DRIVER`: com.mysql.cj.jdbc.Driver
  - `DB_URL`: RDS MySQL 엔드포인트
  - `DB_USERNAME`: 데이터베이스 사용자명
  - `DB_PASSWORD`: 데이터베이스 비밀번호
  - `DDL_AUTO`: update
  - `DB_DIALECT`: org.hibernate.dialect.MySQLDialect
  - `FILE_UPLOAD_DIR`: /var/app/uploads/

### AWS RDS
- **엔진**: MySQL 8.0
- **인스턴스**: db.t3.micro (Free Tier)
- **스토리지**: 20GB gp2
- **리전**: ap-northeast-2 (Seoul)

### 파일 업로드 설정
- **저장 경로**: `/var/app/uploads/` (배포 환경)
- **최대 파일 크기**: 10MB (단일 파일)
- **최대 요청 크기**: 50MB (전체 요청)
- **파일명**: UUID 기반 (충돌 방지)

## 완료된 기능

### 인증 & 사용자
- [x] JWT 기반 인증 시스템
- [x] 사용자 관리 (회원가입, 로그인, 프로필 수정, 탈퇴)
- [x] BCrypt 비밀번호 암호화
- [x] Spring Security + JWT 필터 체인

### 프로젝트 관리
- [x] 프로젝트 생성 및 CRUD
- [x] UUID 기반 초대 코드 시스템
- [x] 프로젝트 멤버 관리 (OWNER/MEMBER 역할)
- [x] 멤버 승인/거절/추방 기능

### 게시글 & 공지사항
- [x] 게시글 CRUD 및 멀티파트 파일 첨부
- [x] 게시글 페이징, 검색, 정렬 (작성일/조회수/댓글수)
- [x] 첨부파일 다운로드
- [x] 프로젝트별 공지사항 시스템

### 투표 시스템
- [x] 일반 투표 생성 (단일/중복 선택)
- [x] 투표하기 및 재투표 기능
- [x] 시간 조율 투표 (날짜/시간 그리드)
- [x] 개인 선택 + 팀 전체 히트맵 동시 제공
- [x] 드래그 방식 시간 선택

### 캘린더
- [x] 캘린더 일정 CRUD
- [x] 일정 참가자 관리
- [x] 7일 이내 마감 일정 조회
- [x] 프로젝트별/전체 일정 조회

### 인프라 & 설정
- [x] AWS Elastic Beanstalk 배포
- [x] MySQL RDS 연동
- [x] dev/prod 프로필 분리
- [x] Swagger API 문서화
- [x] CORS 설정
- [x] Actuator Health Check
- [x] 멀티파트 파일 업로드 (단일 10MB, 전체 50MB)

## Git 브랜치 전략

### 브랜치 구조
```
main        # 프로덕션 브랜치
  └── develop  # 개발 통합 브랜치
       ├── feature/auth-user      (승우)
       ├── feature/post           (준환)
       ├── feature/notice         (준환)
       ├── feature/vote           (준환)
       ├── feature/timepoll       (준환)
       ├── feature/file           (준환)
       ├── feature/project        (재호)
       ├── feature/calendar       (재호)
       ├── fix/user-soft-delete   (버그 수정)
       └── fix/security-cors-config (설정 수정)
```

### 커밋 메시지 규칙
- `feat:` 새로운 기능 추가
- `fix:` 버그 수정
- `chore:` 빌드, 설정 변경
- `docs:` 문서 수정
- `refactor:` 코드 리팩토링

## 기여자

<table>
  <tr>
    <td align="center">
      <b>손승우</b><br>
      인증/보안<br>
      사용자 관리<br>
      인프라 배포
    </td>
    <td align="center">
      <b>전준환</b><br>
      게시판 시스템<br>
      투표 기능<br>
      파일 관리
    </td>
    <td align="center">
      <b>강재호</b><br>
      프로젝트 관리<br>
      캘린더 기능
    </td>
  </tr>
</table>

## 문의
프로젝트 관련 문의사항은 GitHub Issues를 통해 남겨주세요.
