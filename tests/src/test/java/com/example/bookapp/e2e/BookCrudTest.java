package com.example.bookapp.e2e;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class BookCrudTest extends BaseE2ETest {

    private void ensureMode(String mode) {
        if (mode.equals("register")) {
            if (driver.findElements(By.xpath("//button[contains(@class,'toggle-btn') and contains(@class,'active') and contains(.,'Register')]"))
                    .isEmpty()) {
                waitClickable(By.xpath("//button[contains(@class,'toggle-btn') and contains(.,'Register')]"))
                        .click();
            }
        } else {
            if (driver.findElements(By.xpath("//button[contains(@class,'toggle-btn') and contains(@class,'active') and contains(.,'Login')]"))
                    .isEmpty()) {
                waitClickable(By.xpath("//button[contains(@class,'toggle-btn') and contains(.,'Login')]"))
                        .click();
            }
        }
    }

    private void registerAndLogin(String username, String email, String password) {
        driver.get(baseUrl);
        waitVisible(By.cssSelector(".auth-card"));
        ensureMode("register");
        setInput(By.name("username"), username);
        setInput(By.name("email"), email);
        setInput(By.name("fullName"), "E2E CRUD");
        setInput(By.name("password"), password);
        click(By.cssSelector(".primary-btn"));

        waitVisible(By.cssSelector(".auth-card"));
        ensureMode("login");
        setInput(By.name("username"), username);
        setInput(By.name("password"), password);
        click(By.cssSelector(".primary-btn"));

        waitVisible(By.cssSelector(".book-manager-container"));
        ((JavascriptExecutor) driver).executeScript("return 1");
    }

    @Test
    public void shouldCreateUpdateAndDeleteBook() {
        String username = "crud_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String password = "Passw0rd!";

        registerAndLogin(username, email, password);

        String unique = Long.toString(System.currentTimeMillis()).substring(8);
        String title = "Selenium In Action " + unique;
        String author = "Test Author";
        String isbn = "ISBN-" + unique;
        String year = "2024";
        String description = "Automated E2E test book";

        // Use placeholder-based selectors for robustness
        By titleInput = By.cssSelector("input[placeholder='Book Title'], input[name='title']");
        By authorInput = By.cssSelector("input[placeholder='Author'], input[name='author']");
        By isbnInput = By.cssSelector("input[placeholder='ISBN'], input[name='isbn']");
        By yearInput = By.cssSelector("input[placeholder='Publication Year'], input[name='year']");
        By descInput = By.cssSelector("textarea[placeholder='Book Description'], textarea[name='description']");
        By formLocator = By.cssSelector("form.book-form");
        By addButton = By.cssSelector(".book-form .book-btn.primary");

        waitPresent(titleInput);
        waitPresent(authorInput);
        waitPresent(isbnInput);
        waitPresent(yearInput);
        waitPresent(descInput);

        setInput(titleInput, title);
        driver.findElement(titleInput).sendKeys(Keys.TAB);
        setInput(authorInput, author);
        driver.findElement(authorInput).sendKeys(Keys.TAB);
        setInput(isbnInput, isbn);
        driver.findElement(isbnInput).sendKeys(Keys.TAB);
        setInput(yearInput, year);
        driver.findElement(yearInput).sendKeys(Keys.TAB);
        setInput(descInput, description);
        driver.findElement(descInput).sendKeys(Keys.TAB);

        // Submit: prefer button click → fallback requestSubmit on the form
        try {
            waitNotDisabled(addButton);
            click(addButton);
        } catch (Exception e) {
            submitForm(formLocator);
            try { driver.findElement(descInput).sendKeys(Keys.ENTER); } catch (Exception ignored) {}
        }

        // Wait for success or error to diagnose
        String cardXpath = "//div[contains(@class,'book-card')]//h3[normalize-space()='" + title + "']";
        long start = System.currentTimeMillis();
        boolean created = false;
        while (System.currentTimeMillis() - start < Duration.ofSeconds(20).toMillis()) {
            List<WebElement> cards = driver.findElements(By.xpath(cardXpath));
            if (!cards.isEmpty()) { created = true; break; }
            List<WebElement> errs = driver.findElements(By.cssSelector(".error-message"));
            if (!errs.isEmpty()) {
                Assertions.fail("Book create error: " + errs.get(0).getText());
            }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        Assertions.assertThat(created).as("book card should appear after submit").isTrue();

        WebElement card = driver.findElement(By.xpath(cardXpath));
        WebElement editBtn = card.findElement(By.xpath("ancestor::div[contains(@class,'book-card')]//button[contains(@class,'edit')]"));
        editBtn.click();

        String updatedTitle = title + " (Updated)";
        setInput(titleInput, updatedTitle);
        click(addButton);

        waitVisible(By.xpath("//div[contains(@class,'book-card')]//h3[normalize-space()='" + updatedTitle + "']"));

        WebElement updatedCard = driver.findElement(By.xpath("//div[contains(@class,'book-card')]//h3[normalize-space()='" + updatedTitle + "']/ancestor::div[contains(@class,'book-card')]"));
        WebElement deleteBtn = updatedCard.findElement(By.cssSelector(".book-btn.delete"));
        deleteBtn.click();
        driver.switchTo().alert().accept();

        List<WebElement> remaining = driver.findElements(By.xpath("//div[contains(@class,'book-card')]//h3[normalize-space()='" + updatedTitle + "']"));
        Assertions.assertThat(remaining).isEmpty();
    }
} 