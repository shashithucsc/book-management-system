# Run E2E Selenium tests using backend Maven wrapper
param(
    [string]$BaseUrl = "http://localhost:3000",
    [switch]$StartBackend = $true
)

Write-Host "Running BookApp E2E tests..." -ForegroundColor Cyan

$backendProc = $null

if ($StartBackend) {
    Write-Host "Starting backend..." -ForegroundColor Yellow
    $backendProc = Start-Process -FilePath ".\backend\mvnw.cmd" -ArgumentList "spring-boot:run" -WorkingDirectory ".\backend" -WindowStyle Hidden -PassThru

    # Wait for port 8081 up (max 60s)
    $maxWait = 60
    $waited = 0
    while ($waited -lt $maxWait) {
        try {
            $test = Test-NetConnection -ComputerName "localhost" -Port 8081 -WarningAction SilentlyContinue
            if ($test.TcpTestSucceeded) { break }
        } catch {}
        Start-Sleep -Seconds 2
        $waited += 2
    }
    if ($waited -ge $maxWait) {
        Write-Host "Backend did not start within $maxWait seconds. Aborting tests." -ForegroundColor Red
        if ($backendProc) { Stop-Process -Id $backendProc.Id -Force -ErrorAction SilentlyContinue }
        exit 1
    }
    Write-Host "Backend is up on http://localhost:8081" -ForegroundColor Green
}

# Use Maven Wrapper from backend to execute tests/pom.xml
& .\backend\mvnw.cmd -f .\tests\pom.xml test -DbaseUrl=$BaseUrl
$exit = $LASTEXITCODE

if ($StartBackend -and $backendProc) {
    Write-Host "Stopping backend..." -ForegroundColor Yellow
    Stop-Process -Id $backendProc.Id -Force -ErrorAction SilentlyContinue
}

if ($exit -eq 0) {
    Write-Host "E2E tests completed successfully." -ForegroundColor Green
} else {
    Write-Host "E2E tests failed with exit code $exit" -ForegroundColor Red
}

exit $exit 