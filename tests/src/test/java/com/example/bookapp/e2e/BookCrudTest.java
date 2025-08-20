package com.example.bookapp.e2e;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
        waitClickable(By.cssSelector(".primary-btn")).click();

        waitVisible(By.cssSelector(".auth-card"));
        ensureMode("login");
        setInput(By.name("username"), username);
        setInput(By.name("password"), password);
        waitClickable(By.cssSelector(".primary-btn")).click();

        waitVisible(By.cssSelector(".book-manager-container"));
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

        waitForLoadingGone();
        setInput(By.name("title"), title);
        setInput(By.name("author"), author);
        setInput(By.name("isbn"), isbn);
        setInput(By.name("year"), year);
        setInput(By.name("description"), description);
        waitClickable(By.cssSelector(".book-form .book-btn.primary")).click();

        WebElement card = waitVisible(By.xpath("//div[contains(@class,'book-card')]//h3[normalize-space()=" +
                "'" + title + "']"));
        Assertions.assertThat(card.isDisplayed()).isTrue();

        WebElement editBtn = card.findElement(By.xpath("ancestor::div[contains(@class,'book-card')]//button[contains(@class,'edit')]"));
        editBtn.click();

        String updatedTitle = title + " (Updated)";
        setInput(By.name("title"), updatedTitle);
        waitClickable(By.cssSelector(".book-form .book-btn.primary")).click();

        waitVisible(By.xpath("//div[contains(@class,'book-card')]//h3[normalize-space()=" +
                "'" + updatedTitle + "']"));

        WebElement updatedCard = driver.findElement(By.xpath("//div[contains(@class,'book-card')]//h3[normalize-space()=" +
                "'" + updatedTitle + "']/ancestor::div[contains(@class,'book-card')]"));
        WebElement deleteBtn = updatedCard.findElement(By.cssSelector(".book-btn.delete"));
        deleteBtn.click();

        driver.switchTo().alert().accept();

        List<WebElement> remaining = driver.findElements(By.xpath("//div[contains(@class,'book-card')]//h3[normalize-space()=" +
                "'" + updatedTitle + "']"));
        Assertions.assertThat(remaining).isEmpty();
    }
} 