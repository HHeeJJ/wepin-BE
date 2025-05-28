# ❔ Wepin api

wepin의 백엔드 API입니다.

# 개발 API 문서 (Swagger)
https://dev-api.wepin.world/swagger-ui/index.html#/

## 📂 기술 스택

- Java 17
- Spring Boot 2.7.0
- Spring Data JPA
- MariaDB
- Gradle
- Swagger
- Lombok
- AWS EC2, RDS
- GitHub Actions (CI/CD)

## 🚀 배포 및 인프라

- **배포 방식**: AWS EC2 + Nginx + SSL(Let's Encrypt)
- **데이터베이스**: AWS RDS (MySQL)
- **CI/CD**: GitHub Actions로 자동 배포 구성
- **도메인**: Route 53 + 가비아

## 🛠️ 개발 환경

| 항목       | 버전        |
|------------|-------------|
| JDK        | 17.0.1       |
| Spring Boot| 2.7.0        |
| DB         | MariaDB 11.7.2 |
| IDE        | IntelliJ     |

## ⚙️ 실행 방법

### 1. 프로젝트 클론
```bash
git clone https://github.com/username/project-name.git
```
### 2. 프로젝트 실행
```
./gradlew bootRun
```

