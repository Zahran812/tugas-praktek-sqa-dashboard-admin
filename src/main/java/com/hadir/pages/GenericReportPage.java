package com.hadir.pages;

import com.hadir.utils.WaitUtils;
import org.openqa.selenium.WebDriver;

/**
 * GenericReportPage provides a reusable concrete implementation of BaseReportPage
 * for all standard table-based Laporan sub-menus (Semua, Izin Terlambat, Izin Pulang Cepat,
 * Cuti, Validasi Lembur, Shifting, Meeting, Perjalanan Dinas, Koreksi, Izin Off).
 */
public class GenericReportPage extends BaseReportPage {

    private final String targetUrl;
    private final String urlFragment;

    public GenericReportPage(WebDriver driver, String targetUrl, String urlFragment) {
        super(driver);
        this.targetUrl = targetUrl;
        this.urlFragment = urlFragment;
    }

    @Override
    public void open() {
        navigateTo(targetUrl);
        WaitUtils.waitUntilUrlContains(driver, urlFragment);
        WaitUtils.waitForSeconds(1);
    }

    public boolean isPageLoaded() {
        return WaitUtils.waitUntilUrlContains(driver, urlFragment) && isTableDisplayed();
    }
}
