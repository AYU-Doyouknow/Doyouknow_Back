FROM eclipse-temurin:17-jdk

COPY build/libs/*.jar app.jar

EXPOSE 443

ENTRYPOINT ["java", "-jar", "app.jar"]
