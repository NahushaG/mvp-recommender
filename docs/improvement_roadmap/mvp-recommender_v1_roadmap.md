# MVP Recommender – Evolution Checklist (v0 → v1)

**Version Summary:**
This document tracks the current implementation (✅ = completed in v0) and planned improvements (☐ = pending) for evolving the **MVP Recommender** project toward a production-grade system.

---

## ✅ Implemented (v0)

### Core Functionality

* ✅ **FPL Service** — Handles fetching player, fixture, and team data from the Fantasy Premier League API
* ✅ **AI Analysis Service** — Sends top players per position to OpenAI API for MVP insights
* ✅ **MVP Recommendation Service** —

    * Generates top MVP players per position
    * Generates budget squad recommendations
* ✅ **Email Notification Service** — Optional email notification support
* ✅ **Scheduler Service** — Periodic task scheduling for automated updates

---

## 🚀 Improvement Checklist

### 🧱 Code & Architecture Improvements

#### Project Structure & Modularity

* ✅ Initial project structure in Groovy/Java
* ☐ Restructure project for modular architecture (backend + future frontend separation)
* ☐ Revisit architecture per version for scalability and maintainability

#### API & Service Layer

* ☐ Add robust input validation
* ☐ Add proper error handling and custom exception responses
* ☐ Implement rate limiting and throttling
* ☐ Swagger/OpenAPI documentation

#### Database & Persistence

* ✅ Basic PostgreSQL integration
* ☐ Add connection pooling (HikariCP) for performance
* ☐ Add Flyway or Liquibase for schema migration management
* ☐ Add soft deletes and audit fields (`createdAt`, `updatedAt`)

#### Security & Authorization

* ☐ Add JWT-based authentication for endpoints
* ☐ Enforce role-based access control (RBAC)
* ☐ Store secrets securely (Vault or environment secrets)

#### Logging & Monitoring

* ☐ Integrate SLF4J + Logback for structured logging
* ☐ Include correlation IDs for traceability
* ☐ Add monitoring and metrics (Micrometer + Prometheus)

#### Performance & Reliability

* ☐ Implement caching (Redis/in-memory) for FPL data and AI results
* ☐ Add retry/fallback for external API calls (Resilience4j/Spring Retry)
* ☐ Support async processing for AI calls

#### AI Integration

* ☐ Introduce **Prompt Builder Service** to construct dynamic prompts
* ☐ Add rate limit and token usage tracking
* ☐ Validate and sanitize AI output before returning responses

#### Frontend / UI

* ☐ Build React frontend to visualize squads and MVPs
* ☐ Add filters, sorting, and export options
* ☐ Integrate with backend APIs via versioned endpoints (`/api/v1/...`)

#### CI/CD & Deployment

* ☐ Setup GitHub Actions / GitLab CI for:

    * Code quality & lint checks
    * Unit & integration tests
    * Build & Docker image creation
* ☐ Use Docker + docker-compose for local dev
* ☐ Deploy using Kubernetes or managed cloud services

---

### 🧪 Test Improvements

#### Unit Tests

* ☐ **Service Layer**

    * `MvpRecommendationService` — AI mapping, budget squad logic, input handling
    * `FPLDataService` — API parsing, invalid/missing data handling
    * `NotificationService` — Email logic and failure handling
* ☐ **Utilities** — Prompt builder, DTO mappers

#### Integration Tests

* ☐ **Database** — CRUD operations, schema verification
* ☐ **Controller endpoints** — Valid/invalid requests, status code, response validation
* ☐ **External APIs** — Mock FPL API and OpenAI GPT responses

#### Edge & Negative Cases

* ☐ Handle API timeouts/failures
* ☐ Null and invalid input parameters
* ☐ Unexpected or malformed responses

#### Performance & Load Testing

* ☐ Simulate multiple concurrent requests
* ☐ Benchmark AI prompt latency
* ☐ Measure DB query performance

#### Regression & Snapshot Testing

* ☐ Preserve AI response structure compatibility
* ☐ Maintain API contract consistency for frontend

---

### ⚙️ Non-Functional Requirements (NFR)

* ☐ Structured logging & tracing (SLF4J, correlation IDs)
* ☐ Caching for repeated AI results
* ☐ Rate limiting & throttling
* ☐ Exception monitoring (Sentry, NewRelic, etc.)
* ☐ API Gateway for routing & security
* ☐ Multi-environment configuration (dev, staging, prod)

---

## 🏷️ Ticket Abbreviations

| Category              | Abbreviation | Description                                      |
| --------------------- | ------------ | -------------------------------------------------|
| Core Features         | CORE-xx      | Core functionality & data flow                   |
| Backend Enhancements  | BE-xx        | Service, DB, or AI logic improvements            |
| Frontend Enhancements | FE-xx        | UI and UX related improvements                   |
| Testing               | TEST-xx      | Unit, integration, or load testing               |
| Non-Functional        | NFR-xx       | Performance, monitoring, deployment              |
| Application           | APP-xx       | Changes assoeciated with complete project        |

---

**Next Milestone:**
🎯 **v1.0 – Validation & Error, Test Case coverage & API documentation**
