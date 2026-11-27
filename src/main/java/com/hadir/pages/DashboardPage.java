package com.hadir.pages;

import com.hadir.config.ConfigurationManager;
import com.hadir.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * DashboardPage handles both Dashboard (/dashboards/dashboard) and Pending (/dashboards/pending) pages,
 * aligned with SIT FIX scenarios TC-ADM-DSH-001..003 and TC-ADM-DSH-PND-001..008.
 */
public class DashboardPage extends BasePage {

    // Dashboard locators (/dashboards/dashboard)
    private final By metricCards = By.xpath("//div[contains(@class, 'MuiCard-root') or contains(@class, 'MuiPaper-root')][.//*[contains(text(), 'Total') or contains(text(), 'Karyawan') or contains(text(), 'Absen') or contains(text(), 'WFH') or contains(text(), 'Cuti') or contains(text(), 'Sakit') or contains(text(), 'Izin')]]");
    private final By allInputFields = By.xpath("//input[not(@type='hidden')]");

    // Pending locators (/dashboards/pending)
    private final By validator1 = By.xpath("//*[contains(text(), 'Validator 1')]");
    private final By validator2 = By.xpath("//*[contains(text(), 'Validator 2')]");
    private final By validator3 = By.xpath("//*[contains(text(), 'Validator 3')]");
    private final By subtableLembur = By.xpath("//*[contains(text(), 'Lembur')]");
    private final By subtableCuti = By.xpath("//*[contains(text(), 'Cuti')]");
    private final By subtableKoreksi = By.xpath("//*[contains(text(), 'Koreksi')]");
    private final By headerNama = By.xpath("//table//th[contains(., 'NAMA') or contains(., 'Nama')]");
    private final By headerTotal = By.xpath("//table//th[contains(., 'TOTAL') or contains(., 'Total')]");

    private final By inputStartDate = By.xpath("//input[contains(@placeholder, 'Start Date') or contains(@placeholder, 'Awal')]");
    private final By inputEndDate = By.xpath("//input[contains(@placeholder, 'End Date') or contains(@placeholder, 'Akhir')]");
    private final By btnSearch = By.xpath("//button[(contains(., 'Search') or contains(., 'Cari')) and not(@disabled)]");
    private final By btnReset = By.xpath("//button[(contains(., 'Reset') or contains(., 'Atur Ulang')) and not(@disabled)]");

    // Detail page Approve / Reject button check (TC-ADM-DSH-PND-005)
    private final By approveRejectButtons = By.xpath("//button[contains(., 'Approve') or contains(., 'Reject') or contains(., 'Setujui') or contains(., 'Tolak')]");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public void openDashboard() {
        navigateTo(ConfigurationManager.getDashboardUrl());
        WaitUtils.waitUntilUrlContains(driver, "dashboards/dashboard");
    }

    public void openPending() {
        navigateTo(ConfigurationManager.getPendingUrl());
        WaitUtils.waitUntilUrlContains(driver, "dashboards/pending");
    }

    public boolean isDashboardLoaded() {
        return WaitUtils.waitUntilUrlContains(driver, "dashboards/dashboard") && isDisplayed(metricCards);
    }

    public boolean areMetricCardsDisplayed() {
        List<WebElement> cards = findElements(metricCards);
        return !cards.isEmpty() && cards.size() >= 4;
    }

    public int getMetricCardsCount() {
        return findElements(metricCards).size();
    }

    public void refreshPage() {
        driver.navigate().refresh();
        WaitUtils.waitUntilUrlContains(driver, "dashboards/dashboard");
    }

    public void setViewportSize(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
        WaitUtils.waitForSeconds(1);
    }

    public boolean hasNoHorizontalOverflow() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Boolean noOverflow = (Boolean) js.executeScript(
                "return document.documentElement.scrollWidth <= window.innerWidth + 20;"
        );
        return Boolean.TRUE.equals(noOverflow);
    }

    public int getFilterInputsCountOnDashboard() {
        return findElements(allInputFields).size();
    }

    public boolean areAllValidatorsDisplayed() {
        return isDisplayed(validator1) && isDisplayed(validator2) && isDisplayed(validator3);
    }

    public boolean arePendingSubtablesDisplayed() {
        return isDisplayed(subtableLembur) && isDisplayed(subtableCuti) && isDisplayed(subtableKoreksi);
    }

    public boolean arePendingTableHeadersDisplayed() {
        return isDisplayed(headerNama) && isDisplayed(headerTotal);
    }

    public void filterPendingByDate(String startDate, String endDate) {
        if (isDisplayed(inputStartDate)) {
            clearAndTypeWithKeys(inputStartDate, startDate);
        }
        if (isDisplayed(inputEndDate)) {
            clearAndTypeWithKeys(inputEndDate, endDate);
        }
        if (isDisplayed(btnSearch)) {
            click(btnSearch);
            WaitUtils.waitForSeconds(1);
        }
    }

    public void clickResetPendingFilter() {
        if (isDisplayed(btnReset)) {
            click(btnReset);
            WaitUtils.waitForSeconds(1);
        }
    }

    public boolean clickEmployeeInPendingList(String employeeName) {
        By employeeLinkLocator = By.xpath("//table//a[contains(., '" + employeeName + "') or contains(@href, '" + employeeName + "')]");
        List<WebElement> matchingLinks = findElements(employeeLinkLocator);
        if (!matchingLinks.isEmpty()) {
            jsClick(matchingLinks.get(0));
            return WaitUtils.waitUntilUrlContains(driver, "v1-");
        }
        return false;
    }

    public boolean clickFirstEmployeeByRouteFragment(String routeFragment) {
        By linkLocator = By.xpath("//table//a[contains(@href, '" + routeFragment + "')]");
        List<WebElement> links = findElements(linkLocator);
        if (!links.isEmpty()) {
            jsClick(links.get(0));
            return WaitUtils.waitUntilUrlContains(driver, routeFragment);
        }
        return false;
    }

    public boolean hasApproveOrRejectButtonsOnDetail() {
        return !findElements(approveRejectButtons).isEmpty();
    }
}
