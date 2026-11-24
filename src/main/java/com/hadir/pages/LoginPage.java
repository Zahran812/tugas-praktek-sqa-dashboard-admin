package com.hadir.pages;

import com.hadir.config.ConfigurationManager;
import com.hadir.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPage represents the authentication page:
 * https://magang.dikahadir.com/authentication/login
 */
public class LoginPage extends BasePage {

    // Locators
    private final By emailInput = By.xpath("//input[@type='email' or @name='email']");
    private final By passwordInput = By.xpath("//input[@type='password' or @name='password']");
    private final By submitButton = By.xpath("//button[@type='submit']");
    private final By errorMessageAlert = By.xpath("//div[contains(@class,'MuiAlert-message') or contains(text(),'Email atau password salah')]");
    private final By pageHeading = By.xpath("//h4[contains(text(),'Login') or contains(text(),'Masuk')] | //img[contains(@alt,'logo')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(ConfigurationManager.getLoginUrl());
        return this;
    }

    public LoginPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public void clickSubmit() {
        click(submitButton);
    }

    /**
     * Executes login sequence with given credentials
     */
    public void loginAsAdmin(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSubmit();
        WaitUtils.waitUntilUrlContains(driver, "dashboard");
    }

    /**
     * Executes login sequence using default admin credentials from config
     */
    public void loginWithDefaultAdmin() {
        loginAsAdmin(ConfigurationManager.getAdminEmail(), ConfigurationManager.getAdminPassword());
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessageAlert);
    }

    public String getErrorMessageText() {
        return getText(errorMessageAlert);
    }

    public boolean isLoginPageLoaded() {
        return isDisplayed(emailInput) && isDisplayed(passwordInput) && isDisplayed(submitButton);
    }
}
