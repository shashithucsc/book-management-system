param(
    [switch]$RunTests = $false,
    [string]$BaseUrl = "http://localhost:3000"
)

$ErrorActionPreference = 'Stop'

Write-Host "Generating E2E test report..." -ForegroundColor Cyan

# Optionally run tests first
if ($RunTests) {
    Write-Host "Running tests before generating report..." -ForegroundColor Yellow
    & .\backend\mvnw.cmd -f .\tests\pom.xml test -DbaseUrl=$BaseUrl
}

# Generate HTML report using Maven Surefire Report Plugin
Write-Host "Building HTML report (Surefire)..." -ForegroundColor Yellow
& .\backend\mvnw.cmd -f .\tests\pom.xml surefire-report:report

$reportHtml = Join-Path -Path (Resolve-Path .\tests).Path -ChildPath 'target\site\surefire-report.html'
if (!(Test-Path $reportHtml)) {
    Write-Host "Report HTML not found at: $reportHtml" -ForegroundColor Red
    exit 1
}

# Try to locate Chrome or Edge
$chromePaths = @(
    "$Env:ProgramFiles\Google\Chrome\Application\chrome.exe",
    "$Env:ProgramFiles(x86)\Google\Chrome\Application\chrome.exe"
)
$edgePaths = @(
    "$Env:ProgramFiles (x86)\Microsoft\Edge\Application\msedge.exe",
    "$Env:ProgramFiles\Microsoft\Edge\Application\msedge.exe"
)

$pdfOut = Join-Path -Path (Split-Path $reportHtml) -ChildPath 'surefire-report.pdf'

function Print-ToPdf($browserExe, $inputFile, $outputFile) {
    $fileUrl = "file:///" + ($inputFile -replace '\\','/')
    & $browserExe --headless=new --disable-gpu --print-to-pdf="$outputFile" --print-to-pdf-no-header "$fileUrl"
}

$printed = $false
foreach ($c in $chromePaths) {
    if (Test-Path $c) {
        Write-Host "Printing PDF via Chrome..." -ForegroundColor Yellow
        Print-ToPdf -browserExe $c -inputFile $reportHtml -outputFile $pdfOut
        $printed = $true; break
    }
}

if (-not $printed) {
    foreach ($e in $edgePaths) {
        if (Test-Path $e) {
            Write-Host "Printing PDF via Edge..." -ForegroundColor Yellow
            Print-ToPdf -browserExe $e -inputFile $reportHtml -outputFile $pdfOut
            $printed = $true; break
        }
    }
}

Write-Host "HTML report: $reportHtml" -ForegroundColor Green
if (Test-Path $pdfOut) {
    Write-Host "PDF report:  $pdfOut" -ForegroundColor Green
} else {
    Write-Host "PDF generation skipped (Chrome/Edge not found). You can open the HTML or install Chrome/Edge to enable PDF output." -ForegroundColor Yellow
}
