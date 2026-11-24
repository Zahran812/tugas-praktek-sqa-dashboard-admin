package com.hadir.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigurationManager handles loading application configurations
 * from config.properties and system properties.
 */
public class ConfigurationManager {

    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = ConfigurationManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("config.properties file not found in resources folder");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file", e);
        }
    }

    public static String getProperty(String key) {
        // Allow command line override via -Dkey=value
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.isEmpty()) {
            return systemProperty;
        }
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public static String getBaseUrl() {
        return getProperty("base.url", "https://magang.dikahadir.com/");
    }

    public static String getLoginUrl() {
        return getProperty("login.url", "https://magang.dikahadir.com/authentication/login");
    }

    public static String getAdminEmail() {
        return getProperty("admin.email", "admin@hadir.com");
    }

    public static String getAdminPassword() {
        return getProperty("admin.password", "MagangSQA_JC@123");
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "true"));
    }

    public static int getExplicitTimeout() {
        return Integer.parseInt(getProperty("timeout.explicit", "15"));
    }

    public static int getImplicitTimeout() {
        return Integer.parseInt(getProperty("timeout.implicit", "10"));
    }
}
