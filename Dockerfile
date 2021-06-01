################# SDK ####################
FROM maven:3.6.3-openjdk-16 as builder
WORKDIR /root/app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src

RUN mvn -e -DskipTests clean package
WORKDIR /extracted
RUN jar -xf /root/app/target/blogAPI-0.0.1-SNAPSHOT.jar

################# RUNTIME ####################
FROM adoptopenjdk/openjdk16:alpine-jre
RUN mkdir /app

################# SECURITY ####################
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
RUN chown -R appuser:appuser /app
RUN chown -R appuser:appuser /tmp
USER appuser

COPY --from=builder /extracted/BOOT-INF/lib /app/lib
COPY --from=builder /extracted/META-INF /app/META-INF
COPY --from=builder /extracted/BOOT-INF/classes /app


################# LABELS ####################
ARG BUILD_DATE
ARG COMMIT_USER
LABEL org.opencontainers.image.created=$BUILD_DATE
LABEL org.opencontainers.image.authors=$COMMIT_USER

################# ENTRYPOINT ####################
ENTRYPOINT ["java", "-Xms128m", "-Xmx128m", "-cp", "app:app/lib/*", "com.example.blogAPI.blogAPI.BlogApiApplication"]
