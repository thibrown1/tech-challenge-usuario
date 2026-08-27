# ===== ESTAGIO 1: build =====
# Imagem com Maven + JDK, usada SO para compilar o projeto e gerar o .jar.
# Essa imagem inteira (com todo o cache de dependencias do Maven) e'
# descartada no final -- ela nao vai para producao.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia primeiro so o pom.xml e baixa as dependencias.
# Isso cria uma camada de cache no Docker: se so o codigo mudar (e nao
# as dependencias), essa camada e' reaproveitada e o build fica mais rapido.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o codigo-fonte e compila, gerando o .jar em target/
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== ESTAGIO 2: execucao =====
# Imagem enxuta, so com o JRE (sem Maven, sem JDK completo, sem
# codigo-fonte). E' essa imagem, bem menor, que efetivamente roda em
# producao / e' distribuida.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia SOMENTE o .jar final gerado no estagio anterior.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
