# ====================================================================
# Estágio 1 — Build: compila e empacota o .jar com Maven.
# ====================================================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Baixa dependências primeiro (camada cacheada enquanto o pom não mudar).
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn -q -B dependency:go-offline

# Compila e empacota (sem testes — homologação roda os testes à parte).
COPY src src
RUN mvn -q -B clean package -DskipTests

# ====================================================================
# Estágio 2 — Runtime: imagem enxuta só com a JRE e o .jar.
# ====================================================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuário não-root por segurança.
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /app/target/recibo-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
