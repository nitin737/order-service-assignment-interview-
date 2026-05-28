# Order Service

A Spring Boot microservice designed to manage order creation, validation, status transitions, and processing.

---

## 🤖 AI Co-creation & Assistance

This project has been co-created and structured with the assistance of AI. The following areas highlight where and how AI was utilized:

1. **Boilerplate & Skeleton Code Generation**
   - **Controllers**: Generated REST API endpoints and method signatures in `OrderController` to handle client requests (CRUD, status transitions, cancellations) before service-layer integration.
   - **Service Layer**: Generated the interface skeletons and base class methods for the order service implementations (without logic) to define clear APIs and signatures early on.

2. **Dockerization & Containerization Setup**
   - **Dockerfile**: Generated the multi-stage build setup to compile and run the Java application.
   - **Docker Compose**: Orchestrated the configuration (`docker-compose.yml`) for the PostgreSQL database container and the Order Service application container, including port mappings, environment variables, and health checks.

---

## 🚀 Running the Application with Docker

To get the application up and running using the Docker environment, execute:

```bash
docker-compose up
```

This will launch:
- A PostgreSQL database instance on port `5432`
- The Order Service application on port `8080`
