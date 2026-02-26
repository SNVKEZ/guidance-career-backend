FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

RUN mkdir -p auth-service proforient-service security-lib

COPY pom.xml .
COPY auth-service/pom.xml auth-service/pom.xml
COPY proforient-service/pom.xml proforient-service/pom.xml
COPY security-lib/pom.xml security-lib/pom.xml

RUN mvn -B dependency:go-offline -DskipTests

COPY . .

RUN mvn -B -pl auth-service -am clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/auth-service/target/*.jar app.jar
EXPOSE 8020
ENTRYPOINT ["java","-jar","app.jar"]
