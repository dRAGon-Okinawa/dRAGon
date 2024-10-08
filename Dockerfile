FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
LABEL org.opencontainers.image.source="https://github.com/dRAGon-Okinawa/dRAGon"

EXPOSE 1985
EXPOSE 8519

RUN mkdir /data
RUN chown app:app /data
COPY backend/build/libs/backend*.jar /dragon.jar

USER app
ENTRYPOINT ["java","-Xmx512m", "-XX:+CrashOnOutOfMemoryError","-jar","-Dspring.profiles.active=prod","/dragon.jar"]