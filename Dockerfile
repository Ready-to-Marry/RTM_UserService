# 1단계: 빌드
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY UserService/ .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
