package com.hadir.base;

import com.hadir.config.ConfigurationManager;
import com.hadir.drivers.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest manages test lifecycle, WebDriver initialization and cleanup.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
        driver.get(ConfigurationManager.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
