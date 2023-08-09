FROM openjdk:17-alpine
VOLUME /tmp
ADD build/libs/*.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/app.jar"]