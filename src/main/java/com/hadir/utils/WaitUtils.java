package com.hadir.utils;

import com.hadir.config.ConfigurationManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils provides robust explicit and fluent wait methods
 * to handle asynchronous Next.js and Material-UI elements.
 */
public class WaitUtils {

    public static WebElement waitUntilVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigurationManager.getExplicitTimeout()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitUntilClickable(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigurationManager.getExplicitTimeout()));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean waitUntilInvisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigurationManager.getExplicitTimeout()));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitUntilUrlContains(WebDriver driver, String fraction) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigurationManager.getExplicitTimeout()));
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
