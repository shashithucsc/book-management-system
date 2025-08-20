# Simple Backend Runner Script
Write-Host "Starting Book Management Backend..." -ForegroundColor Green

# Navigate to backend directory
Set-Location -Path "backend"

# Run the Spring Boot application
Write-Host "Running Spring Boot application..." -ForegroundColor Yellow
./mvnw.cmd spring-boot:run

Write-Host "Backend stopped." -ForegroundColor Red
