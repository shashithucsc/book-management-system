param(
    [Parameter(Mandatory=$false)]
    [string]$DbHost = "db.mqxuqwhfdotevurazduq.supabase.co",
    [Parameter(Mandatory=$true)]
    [string]$Password,
    [Parameter(Mandatory=$false)]
    [int]$DbPort = 5432
)

if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
    Write-Host "psql is not installed or not in PATH. Install PostgreSQL client or use psql from Supabase CLI." -ForegroundColor Yellow
    exit 1
}

 $env:PGPASSWORD = $Password

Write-Host "Applying schema-prod.sql to ${DbHost}:${DbPort}/postgres" -ForegroundColor Green
psql -h $DbHost -U postgres -p $DbPort -d postgres -f "src/main/resources/schema-prod.sql"

Write-Host "Applying data-prod.sql to ${DbHost}:${DbPort}/postgres" -ForegroundColor Green
psql -h $DbHost -U postgres -p $DbPort -d postgres -f "src/main/resources/data-prod.sql"

Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
