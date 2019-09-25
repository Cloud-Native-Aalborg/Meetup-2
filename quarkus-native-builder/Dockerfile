FROM maven:3.6.2-jdk-11-slim AS build

RUN curl -L https://github.com/oracle/graal/releases/download/vm-19.2.0.1/graalvm-ce-linux-amd64-19.2.0.1.tar.gz --output graalvm-ce-linux-amd64-19.2.0.1.tar.gz \
 && tar -xf graalvm-ce-linux-amd64-19.2.0.1.tar.gz -C / \
 && rm -rf graalvm-ce-linux-amd64-19.2.0.1.tar.gz \
 && /graalvm-ce-19.2.0.1/bin/gu install native-image \
 && apt-get update \
 && apt-get install -y gcc zlib1g-dev

ENV GRAALVM_HOME=/graalvm-ce-19.2.0.1

RUN mkdir -p /tmp/ssl-libs/lib \
  && cp /graalvm-ce-19.2.0.1/jre/lib/security/cacerts /tmp/ssl-libs \
  && cp /graalvm-ce-19.2.0.1/jre/lib/amd64/libsunec.so /tmp/ssl-libs/lib/

COPY pom.xml pom.xml

#download dependencies for caching
RUN mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.1.1:go-offline
