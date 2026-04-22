# API Examples

All public business API calls should go through the gateway on port `8080`.

## Login

```powershell
$login = @{
  username = "akash"
  password = "supplychain2026"
} | ConvertTo-Json

$tokenResponse = Invoke-RestMethod `
  -Uri "http://localhost:8080/auth/token" `
  -Method POST `
  -ContentType "application/json" `
  -Body $login

$token = $tokenResponse.accessToken
```

## Dashboard Data

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/dashboard/data" `
  -Headers @{ Authorization = "Bearer $token" }
```

Example fields:

```json
{
  "supplyChainId": 1,
  "maxExposureScore": 78.5,
  "openRiskEvents": 2,
  "criticalOrHighSuppliers": 2,
  "riskEventsBySeverity": {
    "LOW": 0,
    "MEDIUM": 1,
    "HIGH": 1,
    "CRITICAL": 0
  },
  "heatmapLabels": [
    "Shenzhen Display Works",
    "Taiwan Precision Foundry"
  ],
  "heatmapScores": [
    78.0,
    78.0
  ]
}
```

## Run Full Agent Workflow

```powershell
$body = @{
  event = "Hurricane Taiwan may disrupt semiconductor port operations and chip shipments"
  supplyChainId = 1
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/analyze" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $body
```

Expected response shape:

```json
{
  "workflowId": "WF-...",
  "report": {
    "supplyChainId": 1,
    "riskLevel": "HIGH",
    "exposureScore": 68.5,
    "options": [
      {
        "optionType": "INVENTORY_BUFFER",
        "title": "Increase buffer at MUM-PORT-02",
        "confidenceScore": 0.86,
        "estimatedRecoveryDays": 7
      }
    ],
    "executiveSummary": "..."
  },
  "agentTrace": [
    {
      "agentName": "disruption-detector",
      "status": "COMPLETED"
    }
  ]
}
```

## Monitoring Event Poll

```powershell
Invoke-RestMethod http://localhost:8080/monitoring/news/poll `
  -Headers @{ Authorization = "Bearer $token" }
```

## Supplier Data

```powershell
Invoke-RestMethod http://localhost:8080/data/suppliers `
  -Headers @{ Authorization = "Bearer $token" }
```
