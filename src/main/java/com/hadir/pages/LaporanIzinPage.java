package com.hadir.pages;

import com.hadir.config.ConfigurationManager;
import com.hadir.utils.WaitUtils;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

/**
 * LaporanIzinPage handles /laporan/izin.
 * Features: Table with 5 columns: NAMA USER, UNIT, TANGGAL IZIN, NOTES, STATUS.
 * Search by user name, filter by date range, and reset.
 */
public class LaporanIzinPage extends BaseReportPage {

    public static final List<String> EXPECTED_COLUMNS = Arrays.asList(
            "NAMA USER", "UNIT", "TANGGAL IZIN", "NOTES", "STATUS"
    );

    public LaporanIzinPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void open() {
        navigateTo(ConfigurationManager.getLaporanIzinUrl());
        WaitUtils.waitUntilUrlContains(driver, "laporan/izin");
    }

    public boolean isPageLoaded() {
        return WaitUtils.waitUntilUrlContains(driver, "laporan/izin") && isTableDisplayed();
    }

    public boolean verifyTableColumns() {
        List<String> actualHeaders = getHeaderTexts();
        if (actualHeaders.isEmpty()) {
            return false;
        }
        for (String expected : EXPECTED_COLUMNS) {
            boolean found = actualHeaders.stream().anyMatch(header -> header.equalsIgnoreCase(expected));
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
