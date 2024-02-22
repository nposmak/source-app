FROM openjdk:17
ADD target/source-app.jar source-app.jar
ENTRYPOINT ["java", "-jar", "/source-app.jar"]