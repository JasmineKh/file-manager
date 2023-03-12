FROM maven:latest AS maven
LABEL MAINTAINER="yasaman.kh88@gmail.com"
WORKDIR /app
COPY . /app
RUN mvn clean package

FROM openjdk:17-alpine3.14
ARG JAR_FILE=file-manager-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY --from=maven /app/target/${JAR_FILE} /opt/app/
ENTRYPOINT ["java","-jar","file-manager-0.0.1-SNAPSHOT.jar"]