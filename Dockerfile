FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn -B -q dependency:go-offline

COPY src src

RUN mvn -B -q clean package


FROM eclipse-temurin:21-jdk

ENV APP_HOME=/app \
    SPRING_PROFILES_ACTIVE=prod

WORKDIR $APP_HOME

COPY --from=builder /build/target/*jar app.jar

RUN mkdir -p uploads

RUN useradd -m appuser && chown -R appuser:appuser $APP_HOME

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]