# 백엔드

Hazard-Hackers 프로젝트의 백엔드입니다. 이 프로젝트는 Java와 Spring Boot를 사용하여 RESTful API를 제공합니다.

## 기술 스택

### 프로그래밍 언어 및 프레임워크

- **[Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)**: Java의 LTS 버전으로, 안정성과 성능이 뛰어난 언어입니다.
- **[Spring Boot](https://spring.io/projects/spring-boot)**: Spring 프레임워크를 기반으로 하는 애플리케이션 프레임워크로, 빠르고 쉽게 Spring 기반의 애플리케이션을 개발할 수 있도록 지원합니다.

### 데이터 접근 및 관리

- **[JPA (Java Persistence API)](https://www.oracle.com/java/technologies/persistence.html)**: 자바 객체와 데이터베이스 테이블 간의 매핑을 지원하는 Java 표준 ORM API입니다.
- **[Lombok](https://projectlombok.org/)**: 자바 코드의 보일러플레이트를 줄여주는 라이브러리로, 게터, 세터, 생성자 등을 자동으로 생성해줍니다.
- **[MySQL](https://www.mysql.com/)**: 오픈 소스 관계형 데이터베이스 관리 시스템입니다.

### 인증 및 보안

- **[JWT (JSON Web Token)](https://jwt.io/)**: 클라이언트와 서버 간의 안전한 인증과 정보 전송을 위한 JSON 기반의 토큰입니다.

### 테스트

- **[Rest-Assured](https://rest-assured.io/)**: RESTful 웹 서비스를 테스트하기 위한 Java 라이브러리입니다.

### 개발 도구

- **[IntelliJ IDEA](https://www.jetbrains.com/idea/)**: Java 개발을 위한 강력한 IDE입니다.

## 시작하기

### 데이터베이스 설정

1. **MySQL 설치**: MySQL이 설치되어 있지 않다면, 공식 웹사이트에서 [MySQL](https://www.mysql.com/)을 설치하세요.
2. **데이터베이스 생성**: MySQL에서 프로젝트용 데이터베이스를 생성합니다. 예를 들어, 데이터베이스 이름을 `hazard_hackers`로 설정할 수 있습니다.

   ```sql
   CREATE DATABASE `hazard_hackers-db`;
   CREATE DATABASE `hazard_hackers-test-db`;
   ```
