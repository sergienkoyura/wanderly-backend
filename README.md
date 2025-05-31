# 🛠️ Wanderly App – Backend

Wanderly is a microservice-based backend system that powers the Wanderly mobile app — enabling dynamic route generation, AR model tracking, gamified city exploration, and real-time user statistics.

This is the **Spring Boot backend** for the Wanderly iOS application.

---

## ⚙️ Features

- JWT-based user authentication with email verification
- Dynamic route and AR checkpoint management
- Route generation based on Genetic Algorithm (Multi-Objective Orienteering Problem)
- City-based user progress tracking
- Event-driven architecture with Kafka
- Email notifications for key user actions
- DB migrations via Flyway
- Spring Cloud microservice setup (Gateway, Config, Discovery)

---

## 🧩 Microservices

- `auth-service` – authentication, registration, JWT, refresh tokens
- `user-service` – profile, progress
- `geo-service` – preferences, cities, routes, AR checkpoints
- `notification-service` – Kafka-driven email notifications
- `gateway-service` – unified API gateway with JWT filtering
- `config-service` – centralized configuration via Git
- `discovery-service` – service registry (Eureka)
- `common-service` – shared DTOs, exceptions, utils

### C4 model: Container Diagram

![C4L2](/assets/с4_2.png)

### ER diagram (combined)

![ER](/assets/er.png)

---

## 🐳 Infrastructure

Managed via Docker Compose:

- `PostgreSQL` – relational storage for all services
- `Redis` – storage for TTLs (verification codes, auth flows) and cached POIs
- `Kafka + Zookeeper` – async communication between services

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
