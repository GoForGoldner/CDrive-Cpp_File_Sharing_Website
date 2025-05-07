FROM openjdk:24-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven
RUN mvn package -DskipTests

FROM openjdk:24-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN apt-get update && apt-get install -y g++ make coreutils
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]