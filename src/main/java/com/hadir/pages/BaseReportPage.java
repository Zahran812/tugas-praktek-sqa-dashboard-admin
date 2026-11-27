package com.hadir.pages;

import com.hadir.utils.WaitUtils;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseReportPage provides a reusable abstract foundation for all 16 sub-menus under Laporan.
 * Encapsulates common elements: search by name, date range pickers, search/reset/filter buttons,
 * standard MUI table rows, headers, empty state, XSS alert guard, and pagination.
 */
public abstract class BaseReportPage extends BasePage {

    protected By inputSearch = By.xpath("//input[contains(@placeholder, 'Cari') or contains(@placeholder, 'Search') or contains(@placeholder, 'Nama') or @type='search']");
    protected By inputStartDate = By.xpath("//input[contains(@placeholder, 'Start Date') or contains(@placeholder, 'Awal') or @name='startDate']");
    protected By inputEndDate = By.xpath("//input[contains(@placeholder, 'End Date') or contains(@placeholder, 'Akhir') or @name='endDate']");
    protected By btnSearch = By.xpath("//button[(contains(., 'Search') or contains(., 'Cari') or @type='submit') and not(@disabled)]");
    protected By btnReset = By.xpath("//button[(contains(., 'Reset') or contains(., 'Atur Ulang')) and not(@disabled)]");
    protected By btnFilter = By.xpath("//button[(contains(., 'Filter') or contains(@aria-label, 'filter') or .//*[local-name()='svg']) and not(@disabled)]");

    protected By table = By.xpath("//table");
    protected By tableHeaders = By.xpath("//table//thead//th");
    protected By tableRows = By.xpath("//table//tbody//tr");
    protected By emptyState = By.xpath("//table//tbody//tr[contains(., 'Tidak ada data') or contains(., 'No data') or contains(., 'Data tidak ditemukan')] | //*[contains(text(), 'Tidak ada data') or contains(text(), 'No data') or contains(text(), '0-0 of 0')]");

    protected By paginationRowsPerPage = By.xpath("//div[contains(@class, 'MuiTablePagination-select') or @aria-haspopup='listbox']");
    protected By btnNextPage = By.xpath("//button[@aria-label='Go to next page' or @title='Go to next page']");

    public BaseReportPage(WebDriver driver) {
        super(driver);
    }

    public abstract void open();

    public boolean isSearchInputDisplayed() {
        return isDisplayed(inputSearch);
    }

    public boolean isDateFilterDisplayed() {
        return isDisplayed(inputStartDate) || isDisplayed(inputEndDate);
    }

    public boolean isSearchButtonDisplayed() {
        return isDisplayed(btnSearch);
    }

    public boolean isResetButtonDisplayed() {
        return isDisplayed(btnReset);
    }

    public boolean isFilterButtonDisplayed() {
        return isDisplayed(btnFilter);
    }

    public boolean isTableDisplayed() {
        return isDisplayed(table);
    }

    public void searchByKeyword(String keyword) {
        if (isDisplayed(inputSearch)) {
            clearAndTypeWithKeys(inputSearch, keyword);
            find(inputSearch).sendKeys(Keys.ENTER);
            clickSearch();
            WaitUtils.waitForSeconds(2);
        }
    }

    public void filterByDateRange(String startDate, String endDate) {
        if (isDisplayed(inputStartDate)) {
            clearAndTypeWithKeys(inputStartDate, startDate);
        }
        if (isDisplayed(inputEndDate)) {
            clearAndTypeWithKeys(inputEndDate, endDate);
        }
        clickSearch();
        WaitUtils.waitForSeconds(1);
    }

    public void filterStartDateOnly(String startDate) {
        if (isDisplayed(inputStartDate)) {
            clearAndTypeWithKeys(inputStartDate, startDate);
        }
        clickSearch();
        WaitUtils.waitForSeconds(1);
    }

    public void clickSearch() {
        if (isDisplayed(btnSearch)) {
            jsClick(btnSearch);
        }
    }

    public void clickReset() {
        if (isDisplayed(btnReset)) {
            jsClick(btnReset);
            WaitUtils.waitForSeconds(1);
        }
    }

    public void changeRowsPerPage(int rowsCount) {
        if (isDisplayed(paginationRowsPerPage)) {
            click(paginationRowsPerPage);
            By optionLocator = By.xpath("//li[@role='option' and normalize-space(.)='" + rowsCount + "']");
            if (isDisplayed(optionLocator)) {
                click(optionLocator);
                WaitUtils.waitForSeconds(1);
            }
        }
    }

    public boolean isNextPageButtonEnabled() {
        List<WebElement> buttons = findElements(btnNextPage);
        return !buttons.isEmpty() && buttons.get(0).isEnabled();
    }

    public void clickNextPage() {
        if (isNextPageButtonEnabled()) {
            click(btnNextPage);
            WaitUtils.waitForSeconds(1);
        }
    }

    public boolean waitForEmptySearchResults(int maxWaitSeconds) {
        long end = System.currentTimeMillis() + (maxWaitSeconds * 1000L);
        while (System.currentTimeMillis() < end) {
            if (getRowCount() == 0) {
                return true;
            }
            WaitUtils.waitForSeconds(1);
        }
        return getRowCount() == 0;
    }

    public int getRowCount() {
        List<WebElement> rows = findElements(tableRows);
        if (rows.size() == 1 && isEmptyStateDisplayed()) {
            return 0;
        }
        return rows.size();
    }

    public boolean waitUntilRowCountIs(int expectedCount, int maxSeconds) {
        long end = System.currentTimeMillis() + (maxSeconds * 1000L);
        while (System.currentTimeMillis() < end) {
            if (getRowCount() == expectedCount) {
                return true;
            }
            WaitUtils.waitForSeconds(1);
        }
        return getRowCount() == expectedCount;
    }

    public List<String> getHeaderTexts() {
        List<WebElement> headerElements = findElements(tableHeaders);
        List<String> headers = new ArrayList<>();
        for (WebElement header : headerElements) {
            String text = header.getText().trim();
            if (!text.isEmpty()) {
                headers.add(text);
            }
        }
        return headers;
    }

    public boolean verifyHeadersContain(String... expectedColumns) {
        List<String> actualHeaders = getHeaderTexts();
        for (String expected : expectedColumns) {
            boolean found = actualHeaders.stream().anyMatch(h -> h.equalsIgnoreCase(expected));
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public boolean openUnitOrFilterAndSelectFirst() {
        By filterFunnelBtn = By.xpath("//button[@type='button' and not(contains(@aria-label,'menu')) and not(contains(@aria-label,'password')) and not(contains(@aria-label,'page')) and not(contains(.,'Reset')) and not(contains(.,'Export')) and .//*[local-name()='svg']]");
        List<WebElement> btns = findElements(filterFunnelBtn);
        if (!btns.isEmpty()) {
            jsClick(btns.get(0));
            WaitUtils.waitForSeconds(1);
            By unitInputInModal = By.xpath("//div[@role='dialog']//input[@type='text'] | //input[@type='text' and @placeholder='']");
            List<WebElement> inputs = findElements(unitInputInModal);
            if (!inputs.isEmpty()) {
                jsClick(inputs.get(0));
                WaitUtils.waitForSeconds(1);
                List<WebElement> options = findElements(By.xpath("//li[@role='option']"));
                if (!options.isEmpty()) {
                    jsClick(options.get(0));
                    WaitUtils.waitForSeconds(1);
                }
            }
            By applyBtn = By.xpath("//div[@role='dialog']//button[contains(.,'Terapkan') or contains(.,'Apply') or contains(.,'Search')]");
            List<WebElement> applyBtns = findElements(applyBtn);
            if (!applyBtns.isEmpty()) {
                jsClick(applyBtns.get(0));
                WaitUtils.waitForSeconds(1);
            }
            return true;
        }
        return false;
    }

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(emptyState);
    }

    public String getSearchInputValue() {
        if (isDisplayed(inputSearch)) {
            String value = find(inputSearch).getAttribute("value");
            return value != null ? value : "";
        }
        return "";
    }

    public boolean isNoAlertPresent() {
        try {
            driver.switchTo().alert();
            return false;
        } catch (NoAlertPresentException e) {
            return true;
        }
    }
}
