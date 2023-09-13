FROM openjdk:11-jdk
ADD /build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=main","-jar","/app.jar"]