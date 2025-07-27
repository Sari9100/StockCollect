다음은 사용자가 구축한 Spring Boot + Notion API 기반 종가 수집 시스템에 대한 설명을 정리한 README.md 템플릿입니다. Docker 기반으로 운영하는 현재 구조를 포함한 전반적인 시스템 설명을 포함했습니다.

⸻

📄 README.md

# 📊 Stock Collector System

Spring Boot 기반의 미국 주식 종가 수집 시스템입니다.  
Yahoo Finance에서 수집한 종가 데이터를 Notion DB에 자동 저장하며, Docker 환경에서 실행됩니다.

---

## 📌 주요 기능

- ✅ Yahoo Finance API로 종가 수집
- ✅ Notion API를 통한 종가 기록 DB 저장
- ✅ 종가 이력 중복 저장 방지 (타이틀 중복 검사)
- ✅ 환경변수 기반 보안 설정
- ✅ Docker 이미지 빌드 및 실행 자동화 스크립트

---

## 🏗️ 시스템 구성

```plaintext
┌─────────────────────┐
│    Spring Boot      │
│   (REST Scheduler)  │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│   Yahoo Finance API │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│     Notion API      │
│ (DB: 종목/가격 관리) │
└─────────────────────┘


⸻

⚙️ 실행 방법

1. .env 파일 생성

루트 디렉토리에 .env 파일을 만들고 다음과 같이 설정합니다:

NOTION_API_TOKEN=ntn_XXXXXXXXXXXXXXXXXXXXXXXX
NOTION_API_VERSION=2022-06-28
NOTION_DATABASE_TICKER_ID=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
NOTION_DATABASE_PRICE_ID=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

2. application.properties 설정

src/main/resources/application.properties:

notion.token=${NOTION_API_TOKEN}
notion.version=${NOTION_API_VERSION}
notion.database.ticker_id=${NOTION_DATABASE_TICKER_ID}
notion.database.price_id=${NOTION_DATABASE_PRICE_ID}


⸻

3. 배포 스크립트 사용

redeploy.sh 스크립트 실행:

chmod +x redeploy.sh
./redeploy.sh

	•	Gradle 빌드
	•	Docker 이미지 빌드
	•	기존 컨테이너 제거 및 재실행

⸻

🐳 Docker 정보

이미지 빌드

docker build -t stock-collector .

컨테이너 실행

docker run -d --name stock-collector --env-file .env stock-collector

로그 확인

docker logs -f stock-collector


⸻

📅 향후 계획
	•	시가/고가/저가 저장 기능 추가
	•	Notion → MongoDB 이중 저장 구조 구성
	•	Slack 알림 연동
	•	테스트 자동화 (MockWebServer + JUnit)

⸻

👨‍💻 개발 환경
	•	Java 17
	•	Spring Boot 3.4.8
	•	Gradle 8.x
	•	Docker Desktop
	•	Notion API
	•	Yahoo Finance Web API

⸻

📂 프로젝트 구조

src/
 ├─ main/
 │   ├─ java/com/example/stockcollect/
 │   │   ├─ config/               # 설정 클래스
 │   │   ├─ controller/           # 추후 확장용 API
 │   │   ├─ model/                # Notion DTO
 │   │   ├─ service/              # NotionService, SchedulerService
 │   │   └─ util/                 # Http 유틸 등
 │   └─ resources/
 │       └─ application.properties
 └─ test/


⸻

🛠️ 작성자
	•	개발자: Sangeun Song
	•	역할: 시스템 설계, 백엔드 구현, Docker 배포 자동화

---

필요에 따라 `.md` 파일로 내보내거나, 문서로 따로 정리해드릴 수도 있습니다.  
원한다면 Markdown 문서를 파일로 만들어 드릴까요?