package com.hadir.tests;

import com.hadir.base.BaseTest;
import com.hadir.config.ConfigurationManager;
import com.hadir.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest covers authentication test scenarios on Hadir Web.
 */
public class LoginTest extends BaseTest {

    @Test(description = "Verify successful admin login with valid email and password", priority = 1)
    public void testLoginSuccessWithValidCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        Assert.assertTrue(loginPage.isLoginPageLoaded(), "Login page elements should be visible");

        loginPage.loginWithDefaultAdmin();
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"), 
                "User should be redirected to dashboard upon successful login");
    }

    @Test(description = "Verify login failure when entering incorrect password", priority = 2)
    public void testLoginFailedWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();

        loginPage.enterEmail(ConfigurationManager.getAdminEmail());
        loginPage.enterPassword("WrongPassword123!");
        loginPage.clickSubmit();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed() || driver.getCurrentUrl().contains("login"),
                "Error notification should appear or user remains on login page");
    }

    @Test(description = "Verify login validation when fields are submitted empty", priority = 3)
    public void testLoginValidationEmptyFields() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();

        loginPage.clickSubmit();
        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "User should remain on login page when submitting empty credentials");
    }
}
