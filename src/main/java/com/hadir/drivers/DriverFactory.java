package com.hadir.drivers;

import com.hadir.config.ConfigurationManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * DriverFactory provides thread-safe WebDriver initialization
 * and teardown following industry best practices.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static synchronized void initDriver() {
        if (driverThreadLocal.get() == null) {
            String browser = ConfigurationManager.getBrowser().toLowerCase();
            boolean headless = ConfigurationManager.isHeadless();

            WebDriver driver;

            switch (browser) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (headless) {
                        firefoxOptions.addArguments("-headless");
                    }
                    driver = new FirefoxDriver(firefoxOptions);
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (headless) {
                        edgeOptions.addArguments("--headless=new");
                    }
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case "chrome":
                default:
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    
                    // Setup download directory in project target folder
                    String downloadPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "downloads";
                    File downloadDir = new File(downloadPath);
                    if (!downloadDir.exists()) {
                        downloadDir.mkdirs();
                    }

                    Map<String, Object> prefs = new HashMap<>();
                    prefs.put("download.default_directory", downloadPath);
                    prefs.put("download.prompt_for_download", false);
                    prefs.put("download.directory_upgrade", true);
                    prefs.put("safebrowsing.enabled", true);
                    chromeOptions.setExperimentalOption("prefs", prefs);

                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--window-size=1920,1080");

                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                    }

                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigurationManager.getImplicitTimeout()));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().window().maximize();

            driverThreadLocal.set(driver);
        }
    }

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initDriver();
        }
        return driverThreadLocal.get();
    }

    public static synchronized void quitDriver() {
        if (driverThreadLocal.get() != null) {
            try {
                driverThreadLocal.get().quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
