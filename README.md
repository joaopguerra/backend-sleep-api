# Backend Sleep Application

This is a Spring Boot-based backend application designed to manage sleep-related data. The project is containerized using Docker and uses Gradle as the build tool.

## Base Project Structure

```
- src/main/java/com/noom/interview/fullstack/sleep/
  - domain/
    - User.java
    - Sleep.java
  - repositories/
    - UserRepository.java
    - SleepRepository.java
  - controllers/
    - UserController.java
    - SleepController.java
- src/main/resources/db/migration/  # Flyway migration scripts
- build.gradle  # Gradle build configuration
- Dockerfile  # Docker image definition
- docker-compose.yml  # Docker Compose configuration
```

## Pre-requisites

- Java 11
- Docker
- Gradle

## How to Build and Run

### 1. Clone the Project at: 
```
https://github.com/joaopguerra/backend-sleep-api.git
```

### 2. Build the Project

```bash
./gradlew build
```

### Run Tests Locally

To run unit tests locally, use the command:

```bash
./gradlew test
```

### Run the Application in Docker

```bash
docker-compose up --build
```

## Endpoints

### SleepController

- **GET /sleeps** - Retrieve all sleep records.
- **GET /sleeps/{id}** - Retrieve a sleep record by ID.
- **GET /sleeps/user/{userId}** - Retrieve sleep records by user ID.
- **POST /sleeps** - Create a new sleep record.
```JSON
{
  "sleepDate": "2025-05-01T22:00:00",
  "bedTime": "2025-05-01T23:59:00",
  "wakeTime": "2025-05-02T08:23:00",
  "feeling": "GOOD",
  "userId": "7c5f1945-de1c-414b-a173-e78732ec3a50"
}
```
- **POST /sleeps/delete/{id}** - Set isDeleted flag as true in a sleep record by ID.
- **GET /sleeps/average/{userId}?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD** - Get average sleep for a user within a date range.

### UserController

- **GET /users** - Retrieve all users.

## Key Features

- **Database Configuration**: Flyway migration scripts located in `src/main/resources/db/migration/`.
- **Entities and Repositories**:
  - `User.java`: Defines the User entity.
  - `UserRepository.java`: User repository interface.
  - `Sleep.java`: Defines the Sleep entity.
  - `SleepRepository.java`: Sleep repository interface.
- **REST API**:
- - `SleepController.java`: Handles sleep-related endpoints.
  - `UserController.java`: Handles user-related endpoints.
- **Dockerized Deployment**: Containerized with Docker and Docker Compose.

## Development Notes

- The application uses **PostgreSQL** as the database.
- Unit tests are excluded during the Docker build process using:

```bash
./gradlew build -x test
```


João Guerra

https://www.linkedin.com/in/joaopguerra-/
