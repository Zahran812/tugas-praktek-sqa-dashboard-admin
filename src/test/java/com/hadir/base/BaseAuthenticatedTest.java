package com.hadir.base;

import com.hadir.drivers.DriverFactory;
import com.hadir.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * BaseAuthenticatedTest initializes a shared WebDriver session per test class
 * and logs in once as the default administrator, avoiding redundant logins and
 * rate-limiting while keeping tests fast and reliable.
 */
public abstract class BaseAuthenticatedTest {

    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUpAuthenticatedSession() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
        performLogin();
    }

    @BeforeMethod(alwaysRun = true)
    public void ensureSessionActive() {
        if (driver == null) {
            DriverFactory.initDriver();
            driver = DriverFactory.getDriver();
            performLogin();
        } else if (driver.getCurrentUrl().contains("/authentication/login")) {
            performLogin();
        }
    }

    private void performLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginWithDefaultAdmin();
        Assert.assertFalse(loginPage.getCurrentUrl().contains("/authentication/login"),
                "Pre-condition failed: Admin login was not successful!");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownAuthenticatedSession() {
        DriverFactory.quitDriver();
    }
}
