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
