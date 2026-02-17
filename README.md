# L9G Mousetrap DNS-01

This file provides context for the `l9g-mousetrap` project.

## Project Overview

This is a Java Spring Boot application that acts as a DNS-01 helper service for Micetro JSONRPC. Its primary purpose is to facilitate the DNS-01 challenge for ACME (Automated Certificate Management Environment), which is used to obtain SSL/TLS certificates automatically.

The application exposes a REST API to create and delete DNS records required for the validation process.

**Key Technologies:**

*   Java 21
*   Spring Boot 3
*   Maven
*   Docker
*   Micetro (for DNS management)

**Architecture:**

The application is a standard Spring Boot application with the following key packages:

*   `config`: Contains Spring configuration classes for security, OpenAPI, etc.
*   `controller`: Contains REST controllers for handling API requests.
*   `handler`: Contains global exception handlers.
*   `token`: Contains classes for handling bearer token authentication.

## Building and Running

### Building from Source

To build the application, you need Java 21 and Maven installed.

```bash
# Clean and build the project
mvn clean package
```

This will create a JAR file in the `target/` directory: `l9g-mousetrap.jar`.

### Running with Docker

The most convenient way to run the application is using Docker and Docker Compose.

1.  **Build the Docker image:**
    A `Dockerfile` is provided in the `docker/` directory. You can build the image with the following command from the project root:

    ```bash
    docker build -t l9g-mousetrap:latest -f docker/Dockerfile .
    ```

2.  **Run with Docker Compose:**
    The `docker-compose.yaml` file in the `docker/` directory can be used to run the application. Note that the service name and image in the provided `docker-compose.yaml` might be incorrect (`l9g-uidgen`). You may need to edit it to use the correct image name (`l9g-mousetrap`).

    Assuming you have a `data/config.yaml` file with the necessary configuration, you can start the service with:

    ```bash
    docker-compose -f docker/docker-compose.yaml up -d
    ```

### Running Locally

You can also run the application directly from the command line:

```bash
java -jar target/l9g-mousetrap.jar
```

## Configuration

The application is configured through `src/main/resources/application.yaml` and an external `data/config.yaml` file. The external file is imported and can override the default settings.

The `data/config.yaml` is where you should put your environment-specific configuration, such as Micetro API credentials and other sensitive data.

The application also provides command-line options for handling encrypted values:

*   `java -jar target/l9g-mousetrap.jar -e <clear text>`: Encrypts the given text.
*   `java -jar target/l9g-mousetrap.jar -g`: Generates a new bearer token.
*   `java -jar target/l9g-mousetrap.jar -i`: Initializes the `data/secret.bin` file for encryption.

## Examples

### add TXT Records

```
curl -X POST http://localhost:8080/api/v1/micetro \
  -H "Authorization: Bearer XYZ" \
  -H "Content-Type: application/json" \
  -d '{"zone": "example.de.", "name": "test1", "data": "hello world."}'

curl -X POST http://localhost:8080/api/v1/micetro \
  -H "Authorization: Bearer XYZ" \
  -H "Content-Type: application/json" \
  -d '{"zone": "example.de.", "name": "test2.dev.example.de", "data": "hello world."}'
```

### remove TXT Records

```
curl -X DELETE http://localhost:8080/api/v1/micetro \
  -H "Authorization: Bearer XYZ" \
  -H "Content-Type: application/json" \
  -d '{"zone": "example.de.", "name": "test1"}'

curl -X POST http://localhost:8080/api/v1/micetro \
  -H "Authorization: Bearer XYZ" \
  -H "Content-Type: application/json" \
  -d '{"zone": "example.de.", "name": "test2.dev.example.de"}'
```

## Development Conventions

*   **Code Style:** The project follows standard Java conventions.
*   **Dependency Management:** Maven is used for dependency management.
*   **Authentication:** The API is secured using bearer tokens.
*   **API Documentation:** The project uses `springdoc-openapi` to generate OpenAPI documentation for the REST API. You can likely access it at `/swagger-ui.html` when the application is running.
*   **Licensing:** The project is licensed under the Apache 2.0 license.

