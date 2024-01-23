FROM openjdk:21
ADD target/spring-tiny-link-generator-api.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]