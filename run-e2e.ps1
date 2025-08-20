# Run E2E Selenium tests using backend Maven wrapper
param(
    [string]$BaseUrl = "http://localhost:3000",
    [switch]$StartBackend = $true,
    [switch]$ReportPdf = $true
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

# Run tests
& .\backend\mvnw.cmd -f .\tests\pom.xml test -DbaseUrl=$BaseUrl
$exit = $LASTEXITCODE

# Always generate HTML report, optionally PDF
Write-Host "Generating test report..." -ForegroundColor Yellow
& .\backend\mvnw.cmd -f .\tests\pom.xml surefire-report:report | Out-Null

# Print terminal summary from surefire XML
$reportsDir = Join-Path -Path (Resolve-Path .\tests).Path -ChildPath 'target\surefire-reports'
$totals = @{ tests=0; failures=0; errors=0; skipped=0 }
if (Test-Path $reportsDir) {
    Get-ChildItem -Path $reportsDir -Filter 'TEST-*.xml' | ForEach-Object {
        try {
            [xml]$xml = Get-Content $_.FullName
            $suite = $xml.testsuite
            if ($suite) {
                $totals.tests += [int]$suite.tests
                $totals.failures += [int]$suite.failures
                $totals.errors += [int]$suite.errors
                $totals.skipped += [int]$suite.skipped
            }
        } catch {}
    }
    Write-Host ("Test Summary -> total: {0}, passed: {1}, failures: {2}, errors: {3}, skipped: {4}" -f `
        $totals.tests, ($totals.tests - $totals.failures - $totals.errors - $totals.skipped), $totals.failures, $totals.errors, $totals.skipped) -ForegroundColor Cyan
}

# Report locations
$reportHtml = Join-Path -Path (Resolve-Path .\tests).Path -ChildPath 'target\site\surefire-report.html'
if (Test-Path $reportHtml) {
    Write-Host "HTML report: $reportHtml" -ForegroundColor Green
}

if ($ReportPdf) {
    if (Test-Path .\generate-test-report.ps1) {
        # Use helper to render PDF without re-running tests
        & .\generate-test-report.ps1 -RunTests:$false -BaseUrl $BaseUrl | Out-Null
    }
}

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