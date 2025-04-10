FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./demo/target/demo-0.0.2.jar /app/MtgCollectionApp.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/app/MtgCollectionApp.jar"]