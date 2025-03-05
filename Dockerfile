# Use OpenJDK 17
FROM openjdk:17
WORKDIR /kicker
COPY target/kicker-0.0.1-SNAPSHOT.jar kicker.jar
CMD ["java", "-jar", "kicker.jar"]
