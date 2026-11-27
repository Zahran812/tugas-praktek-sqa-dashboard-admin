package com.hadir.tests;

import com.hadir.base.BaseAuthenticatedTest;
import com.hadir.config.ConfigurationManager;
import com.hadir.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LaporanTest aligns 1-to-1 with SIT FIX scenarios for core & specialized Laporan sub-menus:
 * - Semua (TC-ADM-REP-ALL-001..006)
 * - Kehadiran (TC-ADM-REP-ACT-001..005)
 * - Izin (TC-ADM-REP-IZN-001..005)
 * - Sakit (TC-ADM-REP-SKT-001..003)
 * - Lembur (TC-ADM-REP-LMB-001..002)
 * - Download Absen (TC-ADM-REP-DWN-001..003)
 * - Download Absen V2 (TC-ADM-REP-DW2-001..002)
 */
public class LaporanTest extends BaseAuthenticatedTest {

    // --- LAPORAN SEMUA (TC-ADM-REP-ALL-001..006) ---

    @Test(description = "TC-ADM-REP-ALL-001..004: Verify Laporan Semua loads table, search, date filter, and reset")
    public void testTC_ADM_REP_ALL_001_to_004_SemuaTableSearchDateAndReset() {
        GenericReportPage semuaPage = new GenericReportPage(driver, ConfigurationManager.getLaporanAllUrl(), "laporan/all");
        semuaPage.open();

        Assert.assertTrue(semuaPage.isPageLoaded(), "TC-ADM-REP-ALL-001: Laporan Semua should display table");

        semuaPage.searchByKeyword("Budi");
        Assert.assertTrue(semuaPage.isTableDisplayed(), "TC-ADM-REP-ALL-002: Table should update after keyword search");

        semuaPage.filterByDateRange("2026-11-01", "2026-11-23");
        Assert.assertTrue(semuaPage.isTableDisplayed(), "TC-ADM-REP-ALL-003: Table should update after date filter");

        semuaPage.clickReset();
        Assert.assertEquals(semuaPage.getSearchInputValue(), "", "TC-ADM-REP-ALL-004: Search input should be cleared on Reset");
    }

    @Test(description = "TC-ADM-REP-ALL-005: Verify pagination rows per page (25) and next page navigation")
    public void testTC_ADM_REP_ALL_005_SemuaPagination() {
        GenericReportPage semuaPage = new GenericReportPage(driver, ConfigurationManager.getLaporanAllUrl(), "laporan/all");
        semuaPage.open();

        semuaPage.changeRowsPerPage(25);
        Assert.assertTrue(semuaPage.getRowCount() <= 25, "Rows rendered should be at most 25");
        if (semuaPage.isNextPageButtonEnabled()) {
            semuaPage.clickNextPage();
        }
        Assert.assertTrue(semuaPage.isTableDisplayed(), "Table should remain displayed after pagination");
    }

    @Test(description = "TC-ADM-REP-ALL-006: Negative - Search non-existent user displays empty state without crash")
    public void testTC_ADM_REP_ALL_006_SemuaSearchNotFoundNegative() {
        GenericReportPage semuaPage = new GenericReportPage(driver, ConfigurationManager.getLaporanAllUrl(), "laporan/all");
        semuaPage.open();

        semuaPage.searchByKeyword("UserTidakAda999ZZZ");
        Assert.assertTrue(semuaPage.waitUntilRowCountIs(0, 6), "Should return 0 rows for non-existent user");
    }

    // --- LAPORAN KEHADIRAN (TC-ADM-REP-ACT-001..005) ---

    @Test(description = "TC-ADM-REP-ACT-001 & 002: Verify Kehadiran has no selfie photo view and map modal can open/close")
    public void testTC_ADM_REP_ACT_001_002_KehadiranMapModalAndNoSelfie() {
        LaporanKehadiranPage kehadiranPage = new LaporanKehadiranPage(driver);
        kehadiranPage.open();

        Assert.assertTrue(kehadiranPage.isPageLoaded(), "Laporan Kehadiran should be loaded");
        Assert.assertTrue(kehadiranPage.hasNoSelfiePhotoView(), "Actual UI should not have selfie photo view");

        if (kehadiranPage.getRowCount() > 0 && kehadiranPage.clickFirstEmployeeForLocationMap()) {
            Assert.assertTrue(kehadiranPage.isMapModalDisplayed(), "TC-ADM-REP-ACT-001: Map modal should open");
            kehadiranPage.closeMapModal();
        }
    }

    @Test(description = "TC-ADM-REP-ACT-003 & 004: Verify Kehadiran search, date filter, and unit/filter modal")
    public void testTC_ADM_REP_ACT_003_004_KehadiranSearchDateAndFilter() {
        LaporanKehadiranPage kehadiranPage = new LaporanKehadiranPage(driver);
        kehadiranPage.open();

        Assert.assertTrue(kehadiranPage.isSearchInputDisplayed(), "TC-ADM-REP-ACT-003: Search input should be visible");
        Assert.assertTrue(kehadiranPage.isDateFilterDisplayed(), "TC-ADM-REP-ACT-003: Date filter should be visible");
        kehadiranPage.searchByKeyword("Ahmad");
        kehadiranPage.filterByDateRange("2026-11-01", "2026-11-23");

        kehadiranPage.openUnitOrFilterAndSelectFirst();
        Assert.assertTrue(kehadiranPage.isTableDisplayed(), "TC-ADM-REP-ACT-004: Table should remain visible after unit filter");
    }

    @Test(description = "TC-ADM-REP-ACT-005: Negative - Special characters / XSS input is sanitized without alert popup")
    public void testTC_ADM_REP_ACT_005_KehadiranXssSanitizationNegative() {
        LaporanKehadiranPage kehadiranPage = new LaporanKehadiranPage(driver);
        kehadiranPage.open();

        kehadiranPage.searchByKeyword("<script>alert(1)</script>");
        Assert.assertTrue(kehadiranPage.isNoAlertPresent(), "No XSS alert popup should be triggered");
        Assert.assertTrue(kehadiranPage.isTableDisplayed(), "Table should remain intact after XSS input sanitization");
    }

    // --- LAPORAN IZIN (TC-ADM-REP-IZN-001..005) ---

    @Test(description = "TC-ADM-REP-IZN-001..004: Verify Laporan Izin 5 columns, search, date filter, and reset")
    public void testTC_ADM_REP_IZN_001_to_004_IzinColumnsSearchDateReset() {
        LaporanIzinPage izinPage = new LaporanIzinPage(driver);
        izinPage.open();

        Assert.assertTrue(izinPage.isPageLoaded(), "TC-ADM-REP-IZN-001: Laporan Izin should be loaded");
        Assert.assertTrue(izinPage.verifyTableColumns(), "Must display NAMA USER, UNIT, TANGGAL IZIN, NOTES, STATUS");

        izinPage.searchByKeyword("Siti");
        izinPage.filterByDateRange("2026-11-01", "2026-11-23");
        izinPage.clickReset();
        Assert.assertEquals(izinPage.getSearchInputValue(), "", "TC-ADM-REP-IZN-004: Reset should clear search input");
    }

    @Test(description = "TC-ADM-REP-IZN-005: Negative - Search user with no izin history yields 0 rows")
    public void testTC_ADM_REP_IZN_005_IzinSearchNotFoundNegative() {
        LaporanIzinPage izinPage = new LaporanIzinPage(driver);
        izinPage.open();

        izinPage.searchByKeyword("UserTanpaIzin99");
        Assert.assertTrue(izinPage.waitUntilRowCountIs(0, 6), "Should return 0 rows for user without izin");
    }

    // --- LAPORAN SAKIT (TC-ADM-REP-SKT-001..003) ---

    @Test(description = "TC-ADM-REP-SKT-001..003: Verify Laporan Sakit table, SKD link preview, and date filter")
    public void testTC_ADM_REP_SKT_001_to_003_SakitSkdAndDateFilter() {
        LaporanSakitPage sakitPage = new LaporanSakitPage(driver);
        sakitPage.open();

        Assert.assertTrue(sakitPage.isPageLoaded(), "TC-ADM-REP-SKT-001: Laporan Sakit should be loaded");
        Assert.assertTrue(sakitPage.isSkdColumnOrAttachmentPresent(), "TC-ADM-REP-SKT-001: FOTO/SKD column or View links should be present");

        sakitPage.filterByDateRange("2026-11-01", "2026-11-23");
        Assert.assertTrue(sakitPage.isTableDisplayed(), "TC-ADM-REP-SKT-003: Table should remain visible after date filter");

        sakitPage.open();
        Assert.assertTrue(sakitPage.openAndVerifyFirstSkdPreview(), "TC-ADM-REP-SKT-002: Clicking SKD View link should open preview cleanly");
    }

    // --- LAPORAN LEMBUR (TC-ADM-REP-LMB-001..002) ---

    @Test(description = "TC-ADM-REP-LMB-001 & 002: Verify Laporan Lembur table and Export Excel execution")
    public void testTC_ADM_REP_LMB_001_002_LemburTableAndExport() {
        LaporanLemburPage lemburPage = new LaporanLemburPage(driver);
        lemburPage.open();

        Assert.assertTrue(lemburPage.isPageLoaded(), "TC-ADM-REP-LMB-001: Laporan Lembur should be loaded");
        Assert.assertTrue(lemburPage.verifyHeadersContain("NAMA KARYAWAN", "TANGGAL LEMBUR", "STATUS"),
                "TC-ADM-REP-LMB-001: Lembur table must display expected headers");
        Assert.assertTrue(lemburPage.clickExportAndVerifyDownload(), "TC-ADM-REP-LMB-002: Export button should trigger download cleanly");
    }

    // --- DOWNLOAD ABSEN & V2 (TC-ADM-REP-DWN-001..003 & DW2-001..002) ---

    @Test(description = "TC-ADM-REP-DWN-001: Verify Download Absen form with Unit, Tipe, and valid date range")
    public void testTC_ADM_REP_DWN_001_DownloadAbsenValidSubmission() {
        DownloadAbsenPage downloadPage = new DownloadAbsenPage(driver);
        downloadPage.open();

        int filesBefore = downloadPage.getDownloadedFilesCount();
        Assert.assertTrue(downloadPage.isDownloadAbsenLoaded(), "Download Absen form (Start Date, Download) should be loaded");
        downloadPage.selectFirstUnitIfPresent();
        downloadPage.selectFirstTipeIfPresent();
        downloadPage.enterDateRange("2026-11-01", "2026-11-23");
        downloadPage.clickDownload();
        downloadPage.waitForNewDownload(filesBefore, 8);
        Assert.assertTrue(downloadPage.getCurrentUrl().contains("laporan/generate-report-type"),
                "Download Absen should process valid submission cleanly");
    }

    @Test(description = "TC-ADM-REP-DWN-002: Negative - Download Absen with empty dates handles gracefully")
    public void testTC_ADM_REP_DWN_002_DownloadAbsenEmptyDateNegative() {
        DownloadAbsenPage downloadPage = new DownloadAbsenPage(driver);
        downloadPage.open();

        downloadPage.selectFirstUnitIfPresent();
        downloadPage.clickDownload();

        Assert.assertTrue(downloadPage.getCurrentUrl().contains("laporan/generate-report-type"),
                "User should remain on Download Absen page without crash when dates are empty");
    }

    @Test(description = "TC-ADM-REP-DWN-003: Negative - Download Absen with reversed date range does not download file")
    public void testTC_ADM_REP_DWN_003_DownloadAbsenReversedDateNegative() {
        DownloadAbsenPage downloadPage = new DownloadAbsenPage(driver);
        downloadPage.open();

        int filesBefore = downloadPage.getDownloadedFilesCount();
        downloadPage.enterDateRange("2026-11-23", "2026-11-01");
        downloadPage.clickDownload();

        Assert.assertTrue(downloadPage.getCurrentUrl().contains("laporan/generate-report-type"),
                "Should handle reversed date range gracefully and remain on page");
        Assert.assertEquals(downloadPage.getDownloadedFilesCount(), filesBefore,
                "No report file should be downloaded on reversed date range");
    }

    @Test(description = "TC-ADM-REP-DW2-001 & 002: Verify Download Absen V2 form, valid date, and empty validation")
    public void testTC_ADM_REP_DW2_001_002_DownloadAbsenV2() {
        DownloadAbsenPage downloadPage = new DownloadAbsenPage(driver);
        downloadPage.openV2();

        Assert.assertTrue(downloadPage.isDownloadAbsenV2Loaded(), "TC-ADM-REP-DW2-001: Download Absen V2 form should be loaded");

        // Negative: click Download without filling dates/unit
        int filesBefore = downloadPage.getDownloadedFilesCount();
        downloadPage.clickDownload();
        Assert.assertEquals(downloadPage.getDownloadedFilesCount(), filesBefore,
                "TC-ADM-REP-DW2-002: Should not download on empty parameters");

        // Positive: fill unit and date range
        downloadPage.selectFirstUnitIfPresent();
        downloadPage.enterDateRange("2026-11-01", "2026-11-23");
        downloadPage.clickDownload();
        Assert.assertTrue(downloadPage.getCurrentUrl().contains("laporan/new-generate"),
                "TC-ADM-REP-DW2-001: Download Absen V2 should handle submission cleanly");
    }
}
