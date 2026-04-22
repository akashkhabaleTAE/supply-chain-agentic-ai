# Runbook

## Start Everything

```powershell
cd C:\Users\akash\Documents\Codex\2026-04-22-you-are-an-expert-spring-boot\supply-chain-agentic-ai
docker compose up -d --build
```

Start frontend in development mode:

```powershell
cd C:\Users\akash\Documents\Codex\2026-04-22-you-are-an-expert-spring-boot\supply-chain-agentic-ai\frontend
npm.cmd run dev
```

Open:

```text
http://localhost:3000
```

## Check Health

```powershell
docker compose ps
Invoke-RestMethod http://localhost:8080/actuator/health
```

Expected gateway status:

```text
UP
```

## Run Smoke Test

```powershell
cd C:\Users\akash\Documents\Codex\2026-04-22-you-are-an-expert-spring-boot\supply-chain-agentic-ai
.\scripts\smoke-test.ps1
```

## Common Issues

### Port 3306 Is Already Used

The Compose file maps MySQL to host port `3307`, so this should not block the project. Services inside Docker still use `mysql:3306`.

### PowerShell Blocks npm

Use:

```powershell
npm.cmd run dev
```

### Browser Login Fails

Check:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

Then hard refresh the dashboard:

```text
Ctrl + F5
```

### Swagger Shows 401

Rebuild the gateway after source changes:

```powershell
docker compose up -d --build gateway
```

Open:

```text
http://localhost:8080/api/docs
```

## Stop Everything

```powershell
docker compose down
```

Remove MySQL data as well:

```powershell
docker compose down -v
```

Use `down -v` only when you intentionally want fresh seed data.
