# ---------- PROFORIENT ----------
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 🔥 ОБЯЗАТЕЛЬНО: создаём директории модулей
RUN mkdir -p auth-service proforient-service security-lib

# Копируем POM-файлы (для кеша)
COPY pom.xml .
COPY auth-service/pom.xml auth-service/pom.xml
COPY proforient-service/pom.xml proforient-service/pom.xml
COPY security-lib/pom.xml security-lib/pom.xml

# Кешируем зависимости
RUN mvn -B dependency:go-offline -DskipTests

# Копируем весь проект
COPY . .

# 🔥 СОБИРАЕМ НУЖНЫЙ МОДУЛЬ
RUN mvn -B -pl proforient-service -am clean package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/proforient-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
