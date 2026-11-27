package com.hadir.pages;

import com.hadir.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.Collections;
import java.util.List;

/**
 * BasePage encapsulates common WebDriver interactions across all page objects.
 */
public abstract class BasePage {

    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    protected WebElement find(By locator) {
        return WaitUtils.waitUntilVisible(driver, locator);
    }

    protected List<WebElement> findElements(By locator) {
        try {
            return driver.findElements(locator);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    protected void click(By locator) {
        WaitUtils.waitUntilClickable(driver, locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = find(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void clearAndTypeWithKeys(By locator, String text) {
        WebElement element = find(locator);
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return find(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return find(locator).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void jsClick(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
