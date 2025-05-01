FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./demo/target/MtgApp-1.3.5.jar /app/MtgCollectionApp.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/app/MtgCollectionApp.jar"]