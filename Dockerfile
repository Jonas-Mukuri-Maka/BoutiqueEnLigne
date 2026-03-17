FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src

# Copier pom.xml en premier pour profiter du cache Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et compiler
COPY src ./src
RUN mvn package -DskipTests -B

# nn STAGE 2 : image de production nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Récupérer uniquement le JAR produit par le stage 1
COPY --from=build /src/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]