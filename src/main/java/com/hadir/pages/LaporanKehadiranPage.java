package com.hadir.pages;

import com.hadir.config.ConfigurationManager;
import com.hadir.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * LaporanKehadiranPage handles /laporan/activity.
 * Features: Presence table, clicking an employee row/name opens the coordinate location map modal.
 * Verified: No selfie image modal exists on this page.
 */
public class LaporanKehadiranPage extends BaseReportPage {

    // Locators specific to Laporan Kehadiran
    private final By modalMapPopup = By.xpath("//div[contains(@class, 'MuiModal-root') or contains(@class, 'MuiDialog-root') or contains(@role, 'dialog')]");
    private final By btnCloseModal = By.xpath("//div[contains(@class, 'MuiModal-root') or contains(@class, 'MuiDialog-root') or contains(@role, 'dialog')]//button[contains(., 'Close') or contains(., 'Tutup') or @aria-label='close' or contains(@class, 'close')]");
    private final By selfieImages = By.xpath("//img[contains(@alt, 'selfie') or contains(@class, 'selfie')]");

    public LaporanKehadiranPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void open() {
        navigateTo(ConfigurationManager.getLaporanActivityUrl());
        WaitUtils.waitUntilUrlContains(driver, "laporan/activity");
    }

    public boolean isPageLoaded() {
        return WaitUtils.waitUntilUrlContains(driver, "laporan/activity") && isTableDisplayed();
    }

    public boolean clickFirstEmployeeForLocationMap() {
        // In MUI table, clicking the employee name or view action triggers map coordinate modal
        By nameCellLocator = By.xpath("//table//tbody//tr[1]//td[contains(@class, 'MuiTableCell')][.//h6 or .//a or .//p or .//button]");
        List<WebElement> elements = findElements(nameCellLocator);
        if (!elements.isEmpty()) {
            jsClick(elements.get(0));
            return isDisplayed(modalMapPopup);
        }
        return false;
    }

    public boolean isMapModalDisplayed() {
        return isDisplayed(modalMapPopup);
    }

    public void closeMapModal() {
        if (isDisplayed(btnCloseModal)) {
            click(btnCloseModal);
            WaitUtils.waitUntilInvisible(driver, modalMapPopup);
        }
    }

    public boolean hasNoSelfiePhotoView() {
        // Confirms actual DOM observation: No selfie photo element is rendered
        return findElements(selfieImages).isEmpty();
    }
}
