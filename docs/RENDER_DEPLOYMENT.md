# Render Deployment

This project includes a Render Blueprint at the repository root:

```text
render.yaml
```

## What The Blueprint Deploys

| Render service | Type | URL |
| --- | --- | --- |
| supply-chain-agentic-ai-dashboard | Static site | https://supply-chain-agentic-ai-dashboard.onrender.com |
| supply-chain-agentic-ai-gateway | Docker web service | https://supply-chain-agentic-ai-gateway.onrender.com |
| supply-chain-agentic-ai-agent | Docker web service | https://supply-chain-agentic-ai-agent.onrender.com |
| supply-chain-agentic-ai-data | Docker web service | https://supply-chain-agentic-ai-data.onrender.com |
| supply-chain-agentic-ai-monitoring | Docker web service | https://supply-chain-agentic-ai-monitoring.onrender.com |
| supply-chain-agentic-ai-optimization | Docker web service | https://supply-chain-agentic-ai-optimization.onrender.com |

## Important Demo Notes

- The Render setup is optimized for a public portfolio demo.
- Backend services use the existing deterministic fallback and data-service H2 seed data.
- No paid database is required for the first live deployment.
- Free Render services may spin down when idle, so the first request can be slow.
- A production deployment should add managed PostgreSQL, persistent Redis/Key Value, and stricter service visibility.

## Deploy Steps

1. Confirm the latest code is pushed to GitHub.

```powershell
git status
git push origin main
```

2. Open this Blueprint link:

```text
https://dashboard.render.com/blueprint/new?repo=https://github.com/akashkhabaleTAE/supply-chain-agentic-ai
```

3. Connect GitHub if Render asks.

4. Select the `main` branch and apply the Blueprint.

5. For `OPENAI_API_KEY`, leave it blank for local fallback mode unless you want OpenAI-backed behavior later.

6. Wait for all services to deploy.

7. Open:

```text
https://supply-chain-agentic-ai-dashboard.onrender.com
```

Default login:

```text
username: akash
password: supplychain2026
```

## Health Checks

Gateway:

```text
https://supply-chain-agentic-ai-gateway.onrender.com/actuator/health
```

Swagger:

```text
https://supply-chain-agentic-ai-gateway.onrender.com/api/docs
```

## If Deploys Are Slow

Each backend is a separate Docker service. The first Blueprint deployment can take several minutes because Render builds each image from source.

If free instance hours become a problem, switch only the gateway and dashboard to public services and move internal services to a paid private network setup.
