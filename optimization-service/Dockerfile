ARG SERVICE_NAME=optimization-service

FROM maven:3.9.9-eclipse-temurin-21 AS build
ARG SERVICE_NAME
WORKDIR /workspace

COPY pom.xml .
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY agent-orchestrator/pom.xml agent-orchestrator/pom.xml
COPY data-service/pom.xml data-service/pom.xml
COPY monitoring-service/pom.xml monitoring-service/pom.xml
COPY optimization-service/pom.xml optimization-service/pom.xml
RUN mvn -B -ntp -pl ${SERVICE_NAME} -am dependency:go-offline

COPY . .
RUN mvn -B -ntp -pl ${SERVICE_NAME} -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
ARG SERVICE_NAME
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar app.jar
ENV JAVA_OPTS=""
EXPOSE 8084
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
