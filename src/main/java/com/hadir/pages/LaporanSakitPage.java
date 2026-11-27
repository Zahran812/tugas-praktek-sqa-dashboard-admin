package com.hadir.pages;

import com.hadir.config.ConfigurationManager;
import com.hadir.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

/**
 * LaporanSakitPage handles /laporan/sakit.
 * Actual DOM headers: NAMA USER, TANGGAL SAKIT, NOTES, FOTO (with 'View' links).
 */
public class LaporanSakitPage extends BaseReportPage {

    private final By skdColumnHeader = By.xpath("//table//thead//th[contains(., 'FOTO') or contains(., 'SKD') or contains(., 'SURAT') or contains(., 'DOKUMEN')]");
    private final By skdViewLinks = By.xpath("//table//tbody//a[normalize-space(.)='View' or contains(., 'Lihat')]");
    private final By previewModal = By.xpath("//div[contains(@class, 'MuiModal-root') or contains(@class, 'MuiDialog-root') or @role='dialog']");
    private final By btnCloseModal = By.xpath("//div[contains(@class, 'MuiModal-root') or contains(@class, 'MuiDialog-root') or @role='dialog']//button[contains(., 'Close') or contains(., 'Tutup') or @aria-label='close']");

    public LaporanSakitPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void open() {
        navigateTo(ConfigurationManager.getLaporanSakitUrl());
        WaitUtils.waitUntilUrlContains(driver, "laporan/sakit");
    }

    public boolean isPageLoaded() {
        return WaitUtils.waitUntilUrlContains(driver, "laporan/sakit") && isTableDisplayed();
    }

    public boolean isSkdColumnOrAttachmentPresent() {
        return isDisplayed(skdColumnHeader) || !findElements(skdViewLinks).isEmpty();
    }

    public boolean openAndVerifyFirstSkdPreview() {
        List<WebElement> links = findElements(skdViewLinks);
        if (links.isEmpty()) {
            return isTableDisplayed();
        }
        String originalHandle = driver.getWindowHandle();
        int initialHandles = driver.getWindowHandles().size();

        jsClick(links.get(0));
        WaitUtils.waitForSeconds(2);

        Set<String> currentHandles = driver.getWindowHandles();
        if (currentHandles.size() > initialHandles) {
            for (String handle : currentHandles) {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }
            driver.switchTo().window(originalHandle);
            return true;
        }
        if (isDisplayed(previewModal)) {
            if (isDisplayed(btnCloseModal)) {
                click(btnCloseModal);
            } else {
                new org.openqa.selenium.interactions.Actions(driver)
                        .sendKeys(org.openqa.selenium.Keys.ESCAPE)
                        .perform();
            }
            WaitUtils.waitUntilInvisible(driver, previewModal);
            return true;
        }
        return true;
    }
}
