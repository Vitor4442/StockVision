# ==========================================
# Estágio 1: Build (Compilação do Projeto)
# ==========================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia primeiro apenas o pom.xml para aproveitar o cache das dependências do Maven
COPY pom.xml .

# Baixa as dependências (fase offline)
RUN mvn dependency:go-offline -B

# Copia o código fonte do projeto
COPY src ./src

# Compila o projeto e gera o arquivo .jar (pula os testes na geração da imagem)
RUN mvn package -DskipTests

# ==========================================
# Estágio 2: Runtime (Execução da Aplicação)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria um usuário não-root por questões de segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia o arquivo .jar gerado no Estágio 1 para a imagem final
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta em que o microserviço roda (ex: 8081 ou 8080)
EXPOSE 8081

# Comando para iniciar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]