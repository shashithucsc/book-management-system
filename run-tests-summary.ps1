param(
    [string]$BaseUrl = "http://localhost:3000",
    [switch]$ShowPassed = $false,
    [int]$MaxFailureStackLines = 8
)

$ErrorActionPreference = 'Stop'

Write-Host "Running tests..." -ForegroundColor Yellow
& .\backend\mvnw.cmd -f .\tests\pom.xml test -DbaseUrl=$BaseUrl
$exitCode = $LASTEXITCODE

$reportsDir = Join-Path -Path (Resolve-Path .\tests).Path -ChildPath 'target\surefire-reports'
if (!(Test-Path $reportsDir)) {
    Write-Host "No surefire reports found at $reportsDir" -ForegroundColor Red
    exit $exitCode
}

# Aggregate totals
$totals = [ordered]@{ tests=0; passed=0; failures=0; errors=0; skipped=0; time=0.0 }
$suites = @()
$failedTests = @()
$skippedTests = @()
$passedTests = @()

Get-ChildItem -Path $reportsDir -Filter 'TEST-*.xml' | ForEach-Object {
    try {
        [xml]$xml = Get-Content $_.FullName
        $suite = $xml.testsuite
        if (!$suite) { return }
        $suiteInfo = [ordered]@{
            name=$suite.name; tests=[int]$suite.tests; failures=[int]$suite.failures; errors=[int]$suite.errors; skipped=[int]$suite.skipped; time=[double]$suite.time
        }
        $suites += New-Object psobject -Property $suiteInfo
        $totals.tests += $suiteInfo.tests
        $totals.failures += $suiteInfo.failures
        $totals.errors += $suiteInfo.errors
        $totals.skipped += $suiteInfo.skipped
        $totals.time += $suiteInfo.time

        $xml.testsuite.testcase | ForEach-Object {
            $tc = $_
            $name = $tc.name
            $cls = $tc.classname
            $time = [double]$tc.time
            if ($tc.failure) {
                $msg = $tc.failure.message
                $text = ($tc.failure.'#text')
                $failedTests += [pscustomobject]@{ status='FAILURE'; name=$name; classname=$cls; time=$time; message=$msg; details=$text }
            } elseif ($tc.error) {
                $msg = $tc.error.message
                $text = ($tc.error.'#text')
                $failedTests += [pscustomobject]@{ status='ERROR'; name=$name; classname=$cls; time=$time; message=$msg; details=$text }
            } elseif ($tc.skipped) {
                $msg = $tc.skipped.message
                $skippedTests += [pscustomobject]@{ status='SKIPPED'; name=$name; classname=$cls; time=$time; message=$msg }
            } else {
                $passedTests += [pscustomobject]@{ status='PASSED'; name=$name; classname=$cls; time=$time }
            }
        }
    } catch {}
}
$totals.passed = $totals.tests - $totals.failures - $totals.errors - $totals.skipped

# Pretty print summary
Write-Host "`n================ Test Summary ================" -ForegroundColor Cyan
Write-Host ("Total: {0}  Passed: {1}  Failures: {2}  Errors: {3}  Skipped: {4}  Time: {5:N2}s" -f `
    $totals.tests, $totals.passed, $totals.failures, $totals.errors, $totals.skipped, $totals.time) `
    -ForegroundColor White

# Suites
Write-Host "`nSuites:" -ForegroundColor Cyan
foreach ($s in $suites | Sort-Object name) {
    $color = if (($s.failures + $s.errors) -gt 0) { 'Red' } elseif ($s.skipped -gt 0) { 'Yellow' } else { 'Green' }
    Write-Host (" - {0}  (tests: {1}, passed: {2}, failures: {3}, errors: {4}, skipped: {5}, time: {6:N2}s)" -f `
        $s.name, $s.tests, ($s.tests - $s.failures - $s.errors - $s.skipped), $s.failures, $s.errors, $s.skipped, $s.time) -ForegroundColor $color
}

# Failures and Errors
if ($failedTests.Count -gt 0) {
    Write-Host "`nFailures/Errors:" -ForegroundColor Red
    foreach ($f in $failedTests) {
        Write-Host (" x {0}.{1}  ({2:N2}s) [{3}]" -f $f.classname, $f.name, $f.time, $f.status) -ForegroundColor Red
        if ($f.message) { Write-Host ("   Message: {0}" -f $f.message) -ForegroundColor DarkRed }
        if ($f.details) {
            $lines = $f.details -split "`n"
            $snippet = $lines | Select-Object -First $MaxFailureStackLines
            foreach ($ln in $snippet) { Write-Host ("   {0}" -f ($ln.Trim())) -ForegroundColor DarkGray }
            if ($lines.Count -gt $MaxFailureStackLines) { Write-Host "   ..." -ForegroundColor DarkGray }
        }
    }
}

# Skipped
if ($skippedTests.Count -gt 0) {
    Write-Host "`nSkipped:" -ForegroundColor Yellow
    foreach ($s in $skippedTests) {
        Write-Host (" ~ {0}.{1}  ({2:N2}s)" -f $s.classname, $s.name, $s.time) -ForegroundColor Yellow
        if ($s.message) { Write-Host ("   Reason: {0}" -f $s.message) -ForegroundColor DarkYellow }
    }
}

# Passed (optional)
if ($ShowPassed -and $passedTests.Count -gt 0) {
    Write-Host "`nPassed:" -ForegroundColor Green
    foreach ($p in $passedTests) {
        Write-Host (" + {0}.{1}  ({2:N2}s)" -f $p.classname, $p.name, $p.time) -ForegroundColor Green
    }
}

Write-Host "`nExit code: $exitCode" -ForegroundColor Cyan
exit $exitCode
