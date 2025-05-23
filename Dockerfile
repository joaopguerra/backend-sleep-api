FROM openjdk:11

WORKDIR /app

COPY build.gradle gradlew settings.gradle ./
COPY gradle/ gradle/

RUN chmod +x gradlew

RUN ./gradlew wrapper

COPY src/ src

RUN ./gradlew build -x test

ENTRYPOINT ["java","-jar","build/libs/sleep-0.0.1-SNAPSHOT.jar"]
