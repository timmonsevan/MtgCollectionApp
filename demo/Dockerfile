FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./demo/target/MtgApp-1.4.3.jar /app/MtgCollectionApp.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/app/MtgCollectionApp.jar"]