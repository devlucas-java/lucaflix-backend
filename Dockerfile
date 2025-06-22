# Estágio de build: compila o aplicativo
FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app

# Copiar o arquivo POM primeiro para aproveitar o cache do Docker
COPY pom.xml .
# Copiar os scripts Maven
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn ./.mvn

# Baixar as dependências (esta etapa será armazenada em cache se o pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar o código-fonte
COPY src ./src

# Compilar e empacotar a aplicação
RUN mvn package -DskipTests

# Estágio final: apenas com o runtime Java necessário
FROM eclipse-temurin:23-jdk
ENV APP_HOME=/app
ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR $APP_HOME

# Copiar o JAR do estágio de build
COPY --from=build /app/target/chica-hot-backend-0.0.1-SNAPSHOT.jar app.jar

# Criar diretório para uploads e definir permissões
RUN mkdir -p $APP_HOME/uploads && chmod 777 $APP_HOME/uploads

# Expor porta
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]