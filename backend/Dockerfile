FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /workspace/target/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8081

USER spring:spring

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]
