#  Auth Service

This microservice is responsible for:

-  Authenticating users via **Keycloak**
-  Issuing and validating **JWT tokens**
-  Producing Kafka user registration and logging events

---

##  Technologies

- Java 21  
- Spring Boot 3.4.4  
- Spring Security (OAuth2 Resource Server)  
- Keycloak (external identity provider)  
- Kafka (event-based communication)  
- PostgreSQL + Liquibase *(optional for local persistence)*  

---

##  Configuration (`application.yml`)

```yaml
keycloak:
  realm: greenpulse
  auth-server-url: http://localhost:8180
  resource: greenpulseclient
  public-client: true
  bearer-only: true
  credentials:
    secret: ${KEYCLOAK_SECRET}
  admin:
    username: ${KEYCLOAK_ADMIN_USERNAME:admin}
    password: ${KEYCLOAK_ADMIN_PASSWORD:admin}

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/greenpulse
```

> Make sure to replace environment variables (e.g., `KEYCLOAK_SECRET`) in a secure way using `.env`.

---

##  Authorization Rules

All endpoints are secured with **JWT** tokens issued by **Keycloak**.

Roles such as `ADMIN`, `MANAGER`, and `USER` are extracted from:

```json
realm_access.roles
```

Example JWT snippet:

```json
"realm_access": {
  "roles": ["USER", "ADMIN"]
}
```

---

##  REST Endpoints

| Method | Endpoint                       | Description                |
|--------|--------------------------------|----------------------------|              
| POST   | `/auth/register`               | Registering user           | 
| POST   | `/auth/login`                  | Logging user               |

---

##  Kafka Integration

This service can produce Kafka events such as **user registration** and perform tasks like:

- Assigning roles
- Broadcasting registration to other services

---

##  Example DTO

```json
{
  "id": "UUID",
  "username": "user",
  "email": "user@example.com",
  "status": "ENABLED",
  "roles": ["USER"]
}
```

---

##  Getting Started

### Start Keycloak, PostgreSQL & Kafka

### Run the service

```bash
./gradlew bootRun
```

> Make sure Kafka, Keycloak and other dependencies are up and configured.

---

##  Useful Links

-  [Keycloak Admin REST API Docs](https://www.keycloak.org/docs-api/21.0.1/rest-api/index.html)
-  [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
-  [JWT Debugger](https://jwt.io/)
-  [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
-  [Prometheus](https://prometheus.io/)
-  [Grafana](https://grafana.com/)

---


##  TODOs

- [ ] Add unit tests for user role logic
- [ ] Add Swagger/OpenAPI documentation (optional)
- [ ] Monitoring
