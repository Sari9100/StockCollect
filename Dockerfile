FROM eclipse-temurin:17-jdk-jammy

# 1. 타임존 설정 (Asia/Seoul)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드된 JAR 복사
COPY build/libs/app.jar app.jar

# 4. 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]