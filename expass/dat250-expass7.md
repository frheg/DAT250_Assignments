# Experiment Assignment 7 - Docker

## Docker installation

I already had Docker installed, but I verified Docker was correctly installed by running:

```bash
docker system info
```

No errors were returned, confirming Docker is properly configured.

## Running the application

### Build and start all services:

```bash
docker-compose up --build
```

### Verify running containers:

```bash
docker ps
```

This should list three running containers:

- `pollapp` (port 8080)
- `redis` (port 6379)
- `rabbitmq` (ports 5672 and 15672)

You can also run `curl http://localhost:8080` to verify the application is accessible. If correct, it should return html content from the Spring Boot application.

### Stop all services:

```bash
docker-compose down
```

### Some changes to make the application work:

In order to make the application and its services work together as a Docker image, I had to change the specify correct URLs in `application.properties` to use the service names defined in `docker-compose.yml` as hostnames.
