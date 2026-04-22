# File Tree

```text
supply-chain-agentic-ai/
├── README.md
├── pom.xml
├── docker-compose.yml
├── .env.example
├── .gitignore
├── .dockerignore
├── docs/
│   ├── ARCHITECTURE.md
│   ├── API_EXAMPLES.md
│   ├── FILE_TREE.md
│   ├── PORTFOLIO_NOTES.md
│   ├── RUNBOOK.md
│   └── assets/
│       └── dashboard-mockup.svg
├── scripts/
│   └── smoke-test.ps1
├── gateway-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/akash/supplychain/gateway/
├── agent-orchestrator/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/akash/supplychain/agent/
│       ├── agents/
│       ├── workflow/
│       ├── dto/
│       ├── client/
│       └── config/
├── data-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/akash/supplychain/data/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entities/
│       ├── repositories/
│       └── service/
├── monitoring-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/akash/supplychain/monitoring/
├── optimization-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/akash/supplychain/optimization/
├── frontend/
│   ├── Dockerfile
│   ├── package.json
│   ├── package-lock.json
│   ├── vite.config.js
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       └── styles.css
├── k8s/
│   ├── 00-namespace.yml
│   ├── 01-configmap.yml
│   ├── 02-secrets.yml
│   ├── 03-mysql.yml
│   ├── 04-redis.yml
│   ├── 05-data-service.yml
│   ├── 06-optimization-service.yml
│   ├── 07-agent-orchestrator.yml
│   ├── 08-monitoring-service.yml
│   ├── 09-gateway-service.yml
│   └── 10-prometheus.yml
└── observability/
    └── prometheus.yml
```
