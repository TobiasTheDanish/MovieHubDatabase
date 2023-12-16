FROM eclipse-temurin:17-alpine
# Copy jar file to root folder
COPY target/app.jar /app.jar
# This is the port for the application to listen on
EXPOSE 7070
#The command to run when the container starts
ENTRYPOINT ["java", "-jar", "/app.jar"]