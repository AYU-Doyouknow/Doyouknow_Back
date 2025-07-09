FROM openjdk:17
COPY build/libs/*.jar app.jar
COPY keystore.p12 keystore.p12
EXPOSE 443
ENTRYPOINT ["java", "-jar", "app.jar"]