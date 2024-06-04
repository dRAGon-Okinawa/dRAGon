FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu
EXPOSE 1985
EXPOSE 1984

RUN mkdir /data
RUN chown app:app /data
COPY build/libs/backend-0.0.0.jar /dragon.jar

USER app
ENTRYPOINT ["java","-Xmx512m", "-XX:+CrashOnOutOfMemoryError","-jar","-Dspring.profiles.active=prod","/dragon.jar"]