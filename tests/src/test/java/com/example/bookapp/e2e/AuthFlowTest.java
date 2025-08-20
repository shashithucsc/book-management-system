package com.example.bookapp.e2e;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class AuthFlowTest extends BaseE2ETest {

    private void ensureMode(String mode) {
        // mode: login | register
        if (mode.equals("register")) {
            // Click register toggle only if not already active
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

    @Test
    public void shouldRegisterAndLoginSuccessfully() {
        String username = "user_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String password = "Passw0rd!";

        driver.get(baseUrl);
        waitVisible(By.cssSelector(".auth-card"));

        // Register
        ensureMode("register");
        setInput(By.name("username"), username);
        setInput(By.name("email"), email);
        setInput(By.name("fullName"), "E2E Test");
        setInput(By.name("password"), password);
        waitClickable(By.cssSelector(".primary-btn")).click();

        // Back to login
        waitVisible(By.cssSelector(".auth-card"));
        ensureMode("login");
        setInput(By.name("username"), username);
        setInput(By.name("password"), password);
        waitClickable(By.cssSelector(".primary-btn")).click();

        // Expect BookManager
        Assertions.assertThat(waitVisible(By.cssSelector(".book-manager-container")).isDisplayed()).isTrue();
    }
} 