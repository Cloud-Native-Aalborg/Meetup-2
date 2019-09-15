FROM maven:3.6.2-jdk-11-slim AS build

RUN curl -L https://github.com/oracle/graal/releases/download/vm-19.1.1/graalvm-ce-linux-amd64-19.1.1.tar.gz --output graalvm-ce-linux-amd64-19.1.1.tar.gz \
 && tar -xvf graalvm-ce-linux-amd64-19.1.1.tar.gz -C / \
 && rm -rf graalvm-ce-linux-amd64-19.1.1.tar.gz \
 && /graalvm-ce-19.1.1/bin/gu install native-image \
 && apt-get update \
 && apt-get install -y gcc zlib1g-dev

ENV GRAALVM_HOME=/graalvm-ce-19.1.1
COPY pom.xml pom.xml

#download dependencies for caching
RUN mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.1.1:go-offline

#copy the rest and build it
COPY src src
RUN mvn verify -Pnative  -Dmaven.test.skip=true


#base image for deployment
FROM cescoffier/native-base:latest
EXPOSE 8081

COPY --from=build target/*-runner /application
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]