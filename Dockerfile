# Build
FROM maven:3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml -Dmaven.test.skip=true clean package

# Package
FROM openjdk:17-alpine
COPY --from=build /home/app/target/paymentgateway-0.0.1-SNAPSHOT.jar /usr/local/lib/paymentgateway.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/paymentgateway.jar"]
