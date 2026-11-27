package com.hadir.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * ConfigurationManager handles loading application configurations
 * from config.properties, a .env file, environment variables, and system properties.
 *
 * <p>Precedence (highest first): system property, environment variable,
 * .env value, config.properties.</p>
 *
 * <p>Credentials are intentionally never hardcoded here and should only be
 * provided through .env or environment variables.</p>
 */
public class ConfigurationManager {

    private static final String ENV_FILE_NAME = ".env";
    private static final Properties properties = new Properties();
    private static final Properties dotenvProperties = new Properties();

    static {
        loadConfigProperties();
        loadDotenv();
    }

    private ConfigurationManager() {
        // Utility class
    }

    private static void loadConfigProperties() {
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

    private static void loadDotenv() {
        Path envFile = resolveEnvFile();
        if (envFile != null) {
            try {
                readDotenvLines(Files.readAllLines(envFile, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load " + envFile.toAbsolutePath(), e);
            }
            return;
        }

        // Fallback: allow a .env packaged on the classpath (e.g. resources/.env).
        try (InputStream inputStream = ConfigurationManager.class
                .getClassLoader()
                .getResourceAsStream(ENV_FILE_NAME)) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    readDotenvLines(reader.lines().toList());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env from classpath", e);
        }
    }

    private static Path resolveEnvFile() {
        String explicitPath = System.getProperty("env.file");
        if (isBlank(explicitPath)) {
            explicitPath = System.getenv("ENV_FILE");
        }
        if (!isBlank(explicitPath)) {
            Path path = Paths.get(explicitPath);
            if (!Files.isRegularFile(path)) {
                throw new IllegalStateException(
                        "ENV_FILE points to a file that does not exist: " + path.toAbsolutePath());
            }
            return path;
        }

        Path defaultPath = Paths.get(System.getProperty("user.dir", "."), ENV_FILE_NAME);
        return Files.isRegularFile(defaultPath) ? defaultPath : null;
    }

    private static void readDotenvLines(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && !line.isEmpty() && line.charAt(0) == '\uFEFF') {
                line = line.substring(1);
            }
            parseDotenvLine(line);
        }
    }

    private static void parseDotenvLine(String rawLine) {
        String line = rawLine.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }
        if (line.startsWith("export ")) {
            line = line.substring("export ".length()).trim();
        }

        int separatorIndex = line.indexOf('=');
        if (separatorIndex <= 0) {
            return;
        }

        String key = line.substring(0, separatorIndex).trim();
        String value = line.substring(separatorIndex + 1).trim();
        if (key.isEmpty()) {
            return;
        }

        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                value = value.substring(1, value.length() - 1);
            }
        }

        dotenvProperties.setProperty(key, value);
    }

    public static String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (!isBlank(systemProperty)) {
            return systemProperty;
        }

        String environmentValue = System.getenv(toEnvKey(key));
        if (!isBlank(environmentValue)) {
            return environmentValue;
        }

        String dotenvValue = dotenvProperties.getProperty(key);
        if (isBlank(dotenvValue)) {
            dotenvValue = dotenvProperties.getProperty(toEnvKey(key));
        }
        if (!isBlank(dotenvValue)) {
            return dotenvValue;
        }

        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private static String getSecret(String envKey, String legacyPropertyKey) {
        String value = System.getProperty(legacyPropertyKey);
        if (isBlank(value)) {
            value = System.getProperty(envKey);
        }
        if (isBlank(value)) {
            value = System.getenv(envKey);
        }
        if (isBlank(value)) {
            value = dotenvProperties.getProperty(envKey);
        }
        if (isBlank(value)) {
            value = dotenvProperties.getProperty(legacyPropertyKey);
        }
        if (isBlank(value)) {
            throw new IllegalStateException(
                    "Kredensial '" + envKey + "' belum diset. Salin .env.example menjadi .env "
                            + "di root project lalu isi nilainya, atau set environment variable "
                            + envKey + ".");
        }
        return value;
    }

    private static String toEnvKey(String key) {
        return key.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "_");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String getBaseUrl() {
        return getProperty("base.url", "https://magang.dikahadir.com/");
    }

    public static String getLoginUrl() {
        return getProperty("login.url", "https://magang.dikahadir.com/authentication/login");
    }

    public static String getDashboardUrl() {
        return getProperty("dashboard.url", "https://magang.dikahadir.com/dashboards/dashboard");
    }

    public static String getPendingUrl() {
        return getProperty("pending.url", "https://magang.dikahadir.com/dashboards/pending");
    }

    public static String getLaporanAllUrl() {
        return getProperty("laporan.all.url", "https://magang.dikahadir.com/laporan/all");
    }

    public static String getLaporanActivityUrl() {
        return getProperty("laporan.activity.url", "https://magang.dikahadir.com/laporan/activity");
    }

    public static String getLaporanIzinUrl() {
        return getProperty("laporan.izin.url", "https://magang.dikahadir.com/laporan/izin");
    }

    public static String getLaporanIzinTerlambatUrl() {
        return getProperty("laporan.izin.terlambat.url", "https://magang.dikahadir.com/laporan/izin-terlambat");
    }

    public static String getLaporanIzinPulangCepatUrl() {
        return getProperty("laporan.izin.pulang.cepat.url", "https://magang.dikahadir.com/laporan/izin-pulang-cepat");
    }

    public static String getLaporanCutiUrl() {
        return getProperty("laporan.cuti.url", "https://magang.dikahadir.com/laporan/cuti");
    }

    public static String getLaporanSakitUrl() {
        return getProperty("laporan.sakit.url", "https://magang.dikahadir.com/laporan/sakit");
    }

    public static String getLaporanLemburUrl() {
        return getProperty("laporan.lembur.url", "https://magang.dikahadir.com/laporan/overtime");
    }

    public static String getLaporanValidasiLemburUrl() {
        return getProperty("laporan.validasi.lembur.url", "https://magang.dikahadir.com/laporan/validation-overtime");
    }

    public static String getLaporanShiftingUrl() {
        return getProperty("laporan.shifting.url", "https://magang.dikahadir.com/laporan/shifting");
    }

    public static String getLaporanMeetingUrl() {
        return getProperty("laporan.meeting.url", "https://magang.dikahadir.com/laporan/meeting");
    }

    public static String getLaporanPerjalananDinasUrl() {
        return getProperty("laporan.perjalanan.dinas.url", "https://magang.dikahadir.com/laporan/perjalanan-dinas");
    }

    public static String getLaporanKoreksiUrl() {
        return getProperty("laporan.koreksi.url", "https://magang.dikahadir.com/laporan/koreksi");
    }

    public static String getLaporanIzinOffUrl() {
        return getProperty("laporan.izin.off.url", "https://magang.dikahadir.com/laporan/izin-off");
    }

    public static String getDownloadAbsenUrl() {
        return getProperty("laporan.download.url", "https://magang.dikahadir.com/laporan/generate-report-type");
    }

    public static String getDownloadAbsenV2Url() {
        return getProperty("laporan.download.v2.url", "https://magang.dikahadir.com/laporan/new-generate");
    }

    public static String getAdminEmail() {
        return getSecret("ADMIN_EMAIL", "admin.email");
    }

    public static String getAdminPassword() {
        return getSecret("ADMIN_PASSWORD", "admin.password");
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
