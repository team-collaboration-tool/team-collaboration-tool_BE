# HM (Hyup-Min) - 팀 협업 관리 시스템

## 📋 프로젝트 개요
**HM(협민)**은 팀 프로젝트 협업을 위한 종합 관리 시스템입니다. 
프로젝트 관리, 게시판, 투표, 일정 관리 등 팀 협업에 필요한 모든 기능을 제공합니다.

- **배포 URL**: http://hyupmin-env.eba-njvpbhpe.us-east-2.elasticbeanstalk.com
- **API 문서**: http://hyupmin-env.eba-njvpbhpe.us-east-2.elasticbeanstalk.com/swagger-ui/index.html
- **Health Check**: http://hyupmin-env.eba-njvpbhpe.us-east-2.elasticbeanstalk.com/actuator/health

## 👥 팀원 및 역할

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
  - 게시글 페이징 및 정렬
  - 공지사항 등록/해제
  - 파일 첨부 기능
  
- **투표 기능**
  - 일반 투표 생성 및 투표하기
  - 투표 옵션 관리
  - 투표 결과 집계
  
- **시간 조율 투표**
  - 시간대별 투표 생성
  - 히트맵 형식의 응답 수집
  - 최적 시간대 조회
  
- **파일 관리**
  - UUID 기반 파일명 생성 (충돌 방지)
  - 파일 업로드/다운로드
  - 게시글/투표 파일 첨부

### 강재호 - 프로젝트 및 일정 관리
- **프로젝트 관리**
  - 프로젝트 생성 및 CRUD
  - UUID 기반 초대 코드 시스템
  - 프로젝트 멤버 관리 (OWNER/MEMBER 역할)
  - 멤버 승인/거절/추방 기능
  
- **캘린더 기능**
  - 일정 생성 및 CRUD
  - 일정 참가자 관리 (Many-to-Many)
  - 7일 이내 마감 일정 조회
  - 프로젝트별/전체 일정 조회
  
## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **Database**: MySQL 8.0 (RDS)
- **Build Tool**: Gradle
- **API Documentation**: Swagger UI (SpringDoc OpenAPI)

### Infrastructure
- **Deployment**: AWS Elastic Beanstalk
- **Database**: AWS RDS MySQL (Free Tier)
- **Instance**: t2.micro
- **CI/CD**: Git-based deployment

### Key Libraries
- `jjwt` (0.11.5) - JWT 토큰 생성/검증
- `lombok` - 코드 간소화
- `spring-boot-starter-validation` - 입력 검증
- `springdoc-openapi` - API 문서화

## 📁 프로젝트 구조

```
src/main/java/com/hyupmin/
├── config/
│   ├── jwt/
│   │   ├── JwtTokenProvider.java      # JWT 토큰 생성/검증
│   │   └── JwtAuthenticationFilter.java # JWT 인증 필터
│   ├── SecurityConfig.java            # Spring Security 설정
│   └── SwaggerConfig.java             # Swagger 설정
│
├── controller/
│   ├── user/UserController.java       # 사용자 API
│   ├── post/PostController.java       # 게시글 API
│   ├── vote/VoteController.java       # 투표 API
│   ├── timepoll/TimePollController.java # 시간투표 API
│   ├── project/ProjectController.java  # 프로젝트 API
│   └── calendar/CalendarController.java # 캘린더 API
│
├── domain/
│   ├── user/User.java                 # 사용자 엔티티
│   ├── post/Post.java                 # 게시글 엔티티
│   ├── vote/
│   │   ├── Vote.java                  # 투표 엔티티
│   │   ├── VoteOption.java            # 투표 옵션
│   │   └── VoteResponse.java          # 투표 응답
│   ├── timepoll/
│   │   ├── TimePoll.java              # 시간투표 엔티티
│   │   └── TimeResponse.java          # 시간투표 응답
│   ├── project/
│   │   ├── Project.java               # 프로젝트 엔티티
│   │   └── ProjectUser.java           # 프로젝트-사용자 중간 테이블
│   ├── calendar/CalendarEvent.java    # 일정 엔티티
│   └── attachmentFile/AttachmentFile.java # 첨부파일 엔티티
│
├── service/                           # 비즈니스 로직
├── repository/                        # JPA Repository
├── dto/                              # 요청/응답 DTO
├── file/FileStore.java               # 파일 저장소 관리
└── exception/                        # 공통 예외 처리
```

## 🚀 실행 방법

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
`src/main/resources/application.yml` 파일 확인
```yaml
jwt:
  secret: ${JWT_SECRET:your-secret-key-min-256-bits}
  expiration: ${JWT_EXPIRATION:86400000}

spring:
  datasource:
    url: ${DB_URL:jdbc:h2:mem:testdb}
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:}
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

## 🔐 API 엔드포인트

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
POST   /api/posts                  # 게시글 작성 (파일 첨부 가능)
GET    /api/posts                  # 게시글 목록 (페이징)
GET    /api/posts/{id}             # 게시글 상세
PUT    /api/posts/{id}             # 게시글 수정
DELETE /api/posts/{id}             # 게시글 삭제
PATCH  /api/posts/{id}/notice      # 공지사항 등록
PATCH  /api/posts/{id}/notice/cancel # 공지사항 해제
GET    /api/posts/notices          # 공지사항 목록
```

### 투표
```
POST   /api/votes                  # 투표 생성
POST   /api/votes/cast             # 투표하기
```

### 시간 조율 투표
```
POST   /api/time-poll              # 시간투표 생성
GET    /api/time-poll/list/{projectId} # 시간투표 목록
GET    /api/time-poll/{pollId}     # 시간투표 상세 (히트맵)
POST   /api/time-poll/submit       # 시간 선택 제출
```

### 캘린더
```
GET    /api/calendar/me            # 내 전체 일정
GET    /api/calendar/projects/{projectId} # 프로젝트 일정
POST   /api/calendar/projects/{projectId} # 일정 생성
GET    /api/calendar/projects/{projectId}/upcoming # 7일 이내 마감 일정
GET    /api/calendar/projects/{projectId}/events/{eventId} # 일정 상세
PUT    /api/calendar/projects/{projectId}/events/{eventId} # 일정 수정
DELETE /api/calendar/projects/{projectId}/events/{eventId} # 일정 삭제
```

## 🔒 보안

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

## 📊 데이터베이스 ERD 주요 관계

```
User (1) ─────< (N) ProjectUser (N) >───── (1) Project
  │                                            │
  │                                            │
  ├─< Post >─── Vote                          │
  │      │                                     │
  │      └─── AttachmentFile                  │
  │                                            │
  ├─< TimePoll >─── TimeResponse              │
  │                                            │
  └─< CalendarEvent (N) >─────< (N) User (참가자)
```

## 🌐 배포 정보

### AWS Elastic Beanstalk
- **플랫폼**: Java 17 (Corretto)
- **인스턴스**: t2.micro
- **리전**: us-east-2 (Ohio)
- **환경 변수**:
  - `JWT_SECRET`: JWT 비밀키
  - `DB_URL`: RDS MySQL 엔드포인트
  - `DB_USERNAME`: 데이터베이스 사용자명
  - `DB_PASSWORD`: 데이터베이스 비밀번호

### AWS RDS
- **엔진**: MySQL 8.0
- **인스턴스**: db.t3.micro (Free Tier)
- **스토리지**: 20GB gp2

## ✅ 완료된 기능

- [x] JWT 기반 인증 시스템
- [x] 사용자 관리 (회원가입, 로그인, 프로필 수정, 탈퇴)
- [x] 프로젝트 생성 및 멤버 관리
- [x] 게시글 CRUD 및 파일 첨부
- [x] 투표 시스템
- [x] 시간 조율 투표 (히트맵)
- [x] 캘린더 일정 관리
- [x] AWS 배포
- [x] Swagger API 문서화
- [x] CORS 설정

## 📝 Git 브랜치 전략

### 브랜치 구조
```
main        # 프로덕션 브랜치
  └── develop  # 개발 통합 브랜치
       ├── feature/auth-user      (승우)
       ├── feature/post           (준환)
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

## 🤝 기여자

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

## 📞 문의
프로젝트 관련 문의사항은 GitHub Issues를 통해 남겨주세요.
