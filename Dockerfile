FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src/ /build/src/
RUN mvn -f /build/pom.xml clean package -Dmaven.test.skip=true

FROM openjdk:11-jre-slim
COPY --from=build /build/target/calendar-0.0.1-SNAPSHOT.jar /usr/local/lib/calendar.jar
EXPOSE 8081
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/usr/local/lib/calendar.jar"]
