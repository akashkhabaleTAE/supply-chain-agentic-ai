# Portfolio Notes

## Positioning

This project is designed for Java backend and full-stack portfolio discussions. It demonstrates practical Spring Boot skills with an AI-themed business workflow that still runs fully offline.

## What To Demo First

1. Open the React dashboard at `http://localhost:3000`.
2. Login with `akash / supplychain2026`.
3. Show risk cards, charts, latest events, and critical suppliers.
4. Run the disruption simulation form.
5. Show the 7-agent trace and mitigation options.
6. Open Swagger at `http://localhost:8080/api/docs`.
7. Show Docker Compose services healthy with `docker compose ps`.
8. Show Prometheus at `http://localhost:9090`.

## Strong Interview Points

- Clear separation between gateway, data ownership, orchestration, monitoring, and optimization.
- Agent workflow is explicit and testable rather than hidden in one large service.
- Local fallback AI keeps the system demonstrable without external billing or secrets.
- Docker Compose reflects realistic dependencies: MySQL, Redis, Prometheus.
- React dashboard consumes live gateway APIs and uses JWT auth.
- Kubernetes manifests show deployment awareness without requiring cloud infrastructure.

## Extension Ideas

- Add Kafka for event streaming between monitoring and orchestrator.
- Add Flyway migrations for versioned schema management.
- Add Testcontainers for MySQL integration tests.
- Add Spring AI OpenAI-backed summaries when `AI_PROVIDER=openai`.
- Add role-based authorization for planner, approver, and executive personas.
- Add PDF export endpoint for `ReporterAgent`.
- Add OpenTelemetry tracing across gateway and services.
