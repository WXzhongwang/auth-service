# auth-service
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY target/auth-service-*.jar app.jar
EXPOSE 8100
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
