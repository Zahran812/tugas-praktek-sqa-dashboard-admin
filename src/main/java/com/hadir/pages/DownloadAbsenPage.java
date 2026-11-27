package com.hadir.pages;

import com.hadir.config.ConfigurationManager;
import com.hadir.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.List;

/**
 * DownloadAbsenPage handles /laporan/generate-report-type (Download Absen)
 * and /laporan/new-generate (Download Absen V2), using actual MUI Autocomplete inputs.
 */
public class DownloadAbsenPage extends BasePage {

    private final By inputStartDate = By.xpath("//input[@placeholder='Start Date']");
    private final By inputEndDate = By.xpath("//input[@placeholder='End Date']");
    private final By inputUnit = By.xpath("//input[@placeholder='Pilih Unit']");
    private final By inputTipe = By.xpath("//input[@placeholder='Pilih Tipe']");
    private final By inputDivisi = By.xpath("//input[@placeholder='Pilih Divisi']");
    private final By autocompleteOptions = By.xpath("//li[@role='option']");

    private final By btnDownload = By.xpath("//button[@type='submit' and contains(., 'Download')]");
    private final By btnReset = By.xpath("//button[contains(., 'Reset')]");
    private final By validationAlert = By.xpath("//div[contains(@class, 'MuiAlert-root') or contains(@role, 'alert') or contains(@class, 'Snackbar') or contains(@class, 'MuiFormHelperText-root') or contains(text(), 'harus') or contains(text(), 'required') or contains(text(), 'Pilih') or contains(text(), 'wajib')]");

    public DownloadAbsenPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        navigateTo(ConfigurationManager.getDownloadAbsenUrl());
        WaitUtils.waitUntilUrlContains(driver, "laporan/generate-report-type");
    }

    public void openV2() {
        navigateTo(ConfigurationManager.getDownloadAbsenV2Url());
        WaitUtils.waitUntilUrlContains(driver, "laporan/new-generate");
    }

    public boolean isDownloadAbsenLoaded() {
        return WaitUtils.waitUntilUrlContains(driver, "laporan/generate-report-type")
                && isDisplayed(inputStartDate)
                && isDisplayed(btnDownload);
    }

    public boolean isDownloadAbsenV2Loaded() {
        return WaitUtils.waitUntilUrlContains(driver, "laporan/new-generate")
                && isDisplayed(inputStartDate)
                && isDisplayed(inputUnit)
                && isDisplayed(btnDownload);
    }

    public void selectFirstUnitIfPresent() {
        selectFirstAutocompleteOption(inputUnit);
    }

    public void selectFirstTipeIfPresent() {
        selectFirstAutocompleteOption(inputTipe);
    }

    private void selectFirstAutocompleteOption(By inputLocator) {
        if (isDisplayed(inputLocator)) {
            click(inputLocator);
            WaitUtils.waitForSeconds(1);
            List<WebElement> options = findElements(autocompleteOptions);
            if (!options.isEmpty()) {
                jsClick(options.get(0));
                WaitUtils.waitForSeconds(1);
            }
        }
    }

    public void enterDateRange(String startDate, String endDate) {
        if (isDisplayed(inputStartDate)) {
            clearAndTypeWithKeys(inputStartDate, startDate);
        }
        if (isDisplayed(inputEndDate)) {
            clearAndTypeWithKeys(inputEndDate, endDate);
        }
    }

    public int getDownloadedFilesCount() {
        File downloadDir = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "downloads");
        File[] files = downloadDir.exists() ? downloadDir.listFiles() : new File[0];
        return files != null ? files.length : 0;
    }

    public void clickDownload() {
        if (isDisplayed(btnDownload)) {
            jsClick(btnDownload);
            WaitUtils.waitForSeconds(3);
        }
    }

    public boolean waitForNewDownload(int beforeCount, int maxSeconds) {
        long end = System.currentTimeMillis() + (maxSeconds * 1000L);
        while (System.currentTimeMillis() < end) {
            if (getDownloadedFilesCount() > beforeCount) {
                return true;
            }
            WaitUtils.waitForSeconds(1);
        }
        return false;
    }

    public void clickReset() {
        if (isDisplayed(btnReset)) {
            jsClick(btnReset);
            WaitUtils.waitForSeconds(1);
        }
    }

    public boolean isValidationDisplayed() {
        return isDisplayed(validationAlert);
    }
}
