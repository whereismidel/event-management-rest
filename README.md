# Event Management REST API

This is a Spring Boot application for managing events, chats, and user authentication. It provides a RESTful API for creating, updating, and retrieving events, chats, and user data.

## Prerequisites

Before running the application, make sure you have the following installed:

- Java 22 or later
- Gradle
- PostgreSQL (or any other compatible database)

## Setup

1. Clone the repository:
  ```
  https://github.com/whereismidel/event-management-rest.git
  ```
2. Navigate to the project directory:
  ```
  cd event-management-rest
  ```
3. Build the project using Maven:
Create a new PostgreSQL database and update the database connection properties in ```src/main/resources/application.properties```:
  ```
  spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
  spring.datasource.username=your_username
  spring.datasource.password=your_password
  ```
4. Set the JWT signing key in ```src/main/resources/application.properties```:
  ```
  token.signing.key=your_secret_key
  ```

## Build
To build the project, run the following Gradle command:
```
gradlew build
```

## Run
After a successful build, you can run the application using the following command:
```
gradlew bootRun
```
The application will start on ```http://localhost:8080```.

## API Documentation
Once the application is running, you can access the Swagger UI documentation at ```http://localhost:8080/swagger-ui/index.html```. This documentation provides detailed information about the available endpoints, request/response payloads, and authentication requirements.
