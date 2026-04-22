$ErrorActionPreference = "Stop"

$gateway = "http://localhost:8080"

Write-Host "Checking gateway health..." -ForegroundColor Cyan
$health = Invoke-RestMethod "$gateway/actuator/health"
if ($health.status -ne "UP") {
    throw "Gateway is not UP"
}

Write-Host "Requesting JWT..." -ForegroundColor Cyan
$login = @{
    username = "akash"
    password = "supplychain2026"
} | ConvertTo-Json

$tokenResponse = Invoke-RestMethod `
    -Uri "$gateway/auth/token" `
    -Method POST `
    -ContentType "application/json" `
    -Body $login

$token = $tokenResponse.accessToken
if ([string]::IsNullOrWhiteSpace($token)) {
    throw "Token was empty"
}

Write-Host "Loading dashboard data..." -ForegroundColor Cyan
$dashboard = Invoke-RestMethod `
    -Uri "$gateway/api/v1/dashboard/data" `
    -Headers @{ Authorization = "Bearer $token" }

Write-Host "Running 7-agent analysis..." -ForegroundColor Cyan
$analysisBody = @{
    event = "Hurricane Taiwan may disrupt semiconductor port operations and chip shipments"
    supplyChainId = 1
} | ConvertTo-Json

$analysis = Invoke-RestMethod `
    -Uri "$gateway/api/v1/analyze" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token" } `
    -Body $analysisBody

if ($analysis.agentTrace.Count -ne 7) {
    throw "Expected 7 agent trace entries, got $($analysis.agentTrace.Count)"
}

Write-Host ""
Write-Host "Smoke test passed" -ForegroundColor Green
Write-Host "Gateway: $($health.status)"
Write-Host "Dashboard max exposure: $($dashboard.maxExposureScore)"
Write-Host "Workflow: $($analysis.workflowId)"
Write-Host "Risk score: $($analysis.report.exposureScore)"
Write-Host "Mitigation options: $($analysis.report.options.Count)"
