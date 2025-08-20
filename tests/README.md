# BookApp E2E Tests (Selenium + JUnit)

This module contains end-to-end UI automation tests for the BookApp using Selenium WebDriver.

## Prerequisites
- Java 17+
- Maven
- Backend running at http://localhost:8081
- Frontend running at http://localhost:3000

## How to run

From the `tests` folder:
```bash
mvn test -DbaseUrl=http://localhost:3000
```

By default, tests use Chrome via WebDriverManager (no manual driver install required). 