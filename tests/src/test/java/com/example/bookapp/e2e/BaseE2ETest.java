package com.example.bookapp.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseE2ETest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl = System.getProperty("baseUrl", "http://localhost:3000");

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        // options.addArguments("--headless=new"); // uncomment for CI headless runs
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void waitInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected WebElement waitEnabled(By locator) {
        return wait.until((ExpectedCondition<WebElement>) d -> {
            WebElement el = d.findElement(locator);
            String disabled = el.getAttribute("disabled");
            if (el.isDisplayed() && el.isEnabled() && (disabled == null || disabled.equals("false"))) {
                return el;
            }
            return null;
        });
    }

    protected void waitForLoadingGone() {
        wait.until((ExpectedCondition<Boolean>) d -> d.findElements(By.cssSelector(".loading")).isEmpty());
    }

    protected void setInput(By locator, String value) {
        WebElement el = waitEnabled(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", el);
        try {
            el.click();
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            el.sendKeys(Keys.DELETE);
            el.sendKeys(value);
        } catch (WebDriverException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value=''", el);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value=arguments[1]", el, value);
            ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input',{bubbles:true}))", el);
        }
    }
} 