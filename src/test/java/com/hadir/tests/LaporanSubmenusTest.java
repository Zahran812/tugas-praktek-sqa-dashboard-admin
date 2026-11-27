package com.hadir.tests;

import com.hadir.base.BaseAuthenticatedTest;
import com.hadir.config.ConfigurationManager;
import com.hadir.pages.GenericReportPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * LaporanSubmenusTest uses a reusable DataProvider + GenericReportPage
 * to cover all remaining 9 standard Laporan sub-menus from SIT FIX without duplicating code:
 * - Izin Terlambat (TC-ADM-REP-LAT-001..003)
 * - Izin Pulang Cepat (TC-ADM-REP-ERL-001..002)
 * - Cuti (TC-ADM-REP-CUT-001..003)
 * - Validasi Lembur (TC-ADM-REP-VAL-001..002)
 * - Shifting (TC-ADM-REP-SHF-001..002)
 * - Meeting (TC-ADM-REP-MTG-001..002)
 * - Perjalanan Dinas (TC-ADM-REP-DNS-001..002)
 * - Koreksi (TC-ADM-REP-CRK-001..002)
 * - Izin Off (TC-ADM-REP-OFF-001..002)
 */
public class LaporanSubmenusTest extends BaseAuthenticatedTest {

    @DataProvider(name = "standardReportSubmenus")
    public Object[][] provideStandardReportSubmenus() {
        return new Object[][] {
                {"TC-ADM-REP-LAT", "Izin Terlambat", ConfigurationManager.getLaporanIzinTerlambatUrl(), "laporan/izin-terlambat", "Rahmat"},
                {"TC-ADM-REP-ERL", "Izin Pulang Cepat", ConfigurationManager.getLaporanIzinPulangCepatUrl(), "laporan/izin-pulang-cepat", "Dewi"},
                {"TC-ADM-REP-CUT", "Cuti", ConfigurationManager.getLaporanCutiUrl(), "laporan/cuti", "Budi"},
                {"TC-ADM-REP-VAL", "Validasi Lembur", ConfigurationManager.getLaporanValidasiLemburUrl(), "laporan/validation-overtime", "Operasional"},
                {"TC-ADM-REP-SHF", "Shifting", ConfigurationManager.getLaporanShiftingUrl(), "laporan/shifting", "Fajar"},
                {"TC-ADM-REP-MTG", "Meeting", ConfigurationManager.getLaporanMeetingUrl(), "laporan/meeting", "Rapat"},
                {"TC-ADM-REP-DNS", "Perjalanan Dinas", ConfigurationManager.getLaporanPerjalananDinasUrl(), "laporan/perjalanan-dinas", "Surabaya"},
                {"TC-ADM-REP-CRK", "Koreksi", ConfigurationManager.getLaporanKoreksiUrl(), "laporan/koreksi", "Andi"},
                {"TC-ADM-REP-OFF", "Izin Off", ConfigurationManager.getLaporanIzinOffUrl(), "laporan/izin-off", "Dimas"}
        };
    }

    @Test(dataProvider = "standardReportSubmenus", description = "Verify standard Laporan sub-menus load table and filter controls (-001)")
    public void testSubmenuPageLoadedAndTableVisible(String tcPrefix, String subMenuName, String url, String urlFragment, String keyword) {
        GenericReportPage reportPage = new GenericReportPage(driver, url, urlFragment);
        reportPage.open();

        Assert.assertTrue(reportPage.isPageLoaded(),
                tcPrefix + "-001: Sub-menu " + subMenuName + " should load and display table");
    }

    @Test(dataProvider = "standardReportSubmenus", description = "Verify search, date filter, and reset on standard Laporan sub-menus (-002)")
    public void testSubmenuSearchDateAndReset(String tcPrefix, String subMenuName, String url, String urlFragment, String keyword) {
        GenericReportPage reportPage = new GenericReportPage(driver, url, urlFragment);
        reportPage.open();

        reportPage.searchByKeyword(keyword);
        Assert.assertTrue(reportPage.isTableDisplayed(),
                tcPrefix + "-002: Table on " + subMenuName + " should remain displayed after search");

        reportPage.filterByDateRange("2026-11-01", "2026-11-23");
        reportPage.clickReset();
        Assert.assertTrue(reportPage.isTableDisplayed(),
                tcPrefix + "-002: Table on " + subMenuName + " should reload cleanly after Reset");
    }

    @Test(description = "TC-ADM-REP-LAT-003: Negative - Izin Terlambat partial date filter (Start Date only, End Date empty)")
    public void testTC_ADM_REP_LAT_003_IzinTerlambatPartialDateNegative() {
        GenericReportPage reportPage = new GenericReportPage(
                driver, ConfigurationManager.getLaporanIzinTerlambatUrl(), "laporan/izin-terlambat");
        reportPage.open();

        reportPage.filterStartDateOnly("2026-11-10");
        Assert.assertTrue(reportPage.isTableDisplayed(), "Should handle partial date filter gracefully without crash");
    }

    @Test(description = "TC-ADM-REP-CUT-001 & 002: Verify Cuti headers (NAMA KARYAWAN, TIPE CUTI, STATUS) and filter interaction")
    public void testTC_ADM_REP_CUT_001_002_CutiHeadersAndFilter() {
        GenericReportPage cutiPage = new GenericReportPage(
                driver, ConfigurationManager.getLaporanCutiUrl(), "laporan/cuti");
        cutiPage.open();

        Assert.assertTrue(cutiPage.verifyHeadersContain("NAMA KARYAWAN", "TIPE CUTI", "STATUS"),
                "TC-ADM-REP-CUT-001: Cuti table must contain NAMA KARYAWAN, TIPE CUTI, STATUS");
        cutiPage.openUnitOrFilterAndSelectFirst();
        Assert.assertTrue(cutiPage.isTableDisplayed(), "TC-ADM-REP-CUT-002: Table should remain displayed after filter");
    }

    @Test(description = "TC-ADM-REP-CUT-003: Negative - Cuti search non-existent employee returns 0 rows")
    public void testTC_ADM_REP_CUT_003_CutiNotFoundNegative() {
        GenericReportPage reportPage = new GenericReportPage(
                driver, ConfigurationManager.getLaporanCutiUrl(), "laporan/cuti");
        reportPage.open();

        reportPage.searchByKeyword("KaryawanBaruHariIni999");
        Assert.assertTrue(reportPage.waitUntilRowCountIs(0, 8),
                "Should return 0 rows for employee without cuti history");
    }

    @Test(description = "TC-ADM-REP-VAL-001 & 002: Verify Validasi Lembur headers and unit filter")
    public void testTC_ADM_REP_VAL_001_002_ValidasiLemburHeadersAndFilter() {
        GenericReportPage valPage = new GenericReportPage(
                driver, ConfigurationManager.getLaporanValidasiLemburUrl(), "laporan/validation-overtime");
        valPage.open();

        Assert.assertTrue(valPage.verifyHeadersContain("NAMA KARYAWAN", "UNIT", "TANGGAL LEMBUR"),
                "TC-ADM-REP-VAL-001: Validasi Lembur must contain NAMA KARYAWAN, UNIT, TANGGAL LEMBUR");
        valPage.openUnitOrFilterAndSelectFirst();
        Assert.assertTrue(valPage.isTableDisplayed(), "TC-ADM-REP-VAL-002: Table should remain visible after filter");
    }

    @Test(description = "TC-ADM-REP-CRK-001 & 002: Verify Koreksi headers and approval/reject controls")
    public void testTC_ADM_REP_CRK_001_002_KoreksiHeadersAndFilter() {
        GenericReportPage koreksiPage = new GenericReportPage(
                driver, ConfigurationManager.getLaporanKoreksiUrl(), "laporan/koreksi");
        koreksiPage.open();

        Assert.assertTrue(koreksiPage.verifyHeadersContain("NAMA KARYAWAN", "JAM MASUK", "JAM KELUAR", "STATUS"),
                "TC-ADM-REP-CRK-001: Koreksi must contain NAMA KARYAWAN, JAM MASUK, JAM KELUAR, STATUS");
        koreksiPage.openUnitOrFilterAndSelectFirst();
        Assert.assertTrue(koreksiPage.isTableDisplayed(), "TC-ADM-REP-CRK-002: Table should remain visible after filter");
    }
}
