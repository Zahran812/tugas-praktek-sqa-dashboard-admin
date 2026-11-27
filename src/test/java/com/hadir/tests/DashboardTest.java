package com.hadir.tests;

import com.hadir.base.BaseAuthenticatedTest;
import com.hadir.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DashboardTest aligns 1-to-1 with SIT FIX Dashboard Menu scenarios:
 * TC-ADM-DSH-001..003 and TC-ADM-DSH-PND-001..008 (11 scenarios).
 */
public class DashboardTest extends BaseAuthenticatedTest {

    @Test(description = "TC-ADM-DSH-001: Verify Dashboard displays metric cards and confirms no search/date filter inputs exist")
    public void testTC_ADM_DSH_001_DashboardMetricsAndNoFilter() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openDashboard();

        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard page should be loaded");
        Assert.assertTrue(dashboardPage.areMetricCardsDisplayed(), "All metric cards should be displayed on Dashboard");
        Assert.assertEquals(dashboardPage.getFilterInputsCountOnDashboard(), 0,
                "Confirmed actual DOM: Dashboard has 0 search/date filter inputs");
    }

    @Test(description = "TC-ADM-DSH-002: Verify Dashboard metric cards remain consistent after page refresh")
    public void testTC_ADM_DSH_002_DashboardMetricsSyncOnRefresh() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openDashboard();

        int initialCards = dashboardPage.getMetricCardsCount();
        dashboardPage.refreshPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard should reload cleanly");
        Assert.assertEquals(dashboardPage.getMetricCardsCount(), initialCards,
                "Metric cards count should remain consistent after refresh");
    }

    @Test(description = "TC-ADM-DSH-003: Verify Dashboard layout responsiveness across desktop (1920x1080) and tablet (768x1024)")
    public void testTC_ADM_DSH_003_DashboardResponsiveViewport() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openDashboard();

        dashboardPage.setViewportSize(1920, 1080);
        Assert.assertTrue(dashboardPage.hasNoHorizontalOverflow(), "Desktop viewport should have no horizontal overflow");

        dashboardPage.setViewportSize(768, 1024);
        Assert.assertTrue(dashboardPage.areMetricCardsDisplayed(), "Metric cards should remain visible on tablet viewport");

        dashboardPage.setViewportSize(1920, 1080);
    }

    @Test(description = "TC-ADM-DSH-PND-001: Verify Pending page displays 3 Validators and Lembur, Cuti, Koreksi subtables")
    public void testTC_ADM_DSH_PND_001_PendingValidatorsAndSubtables() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openPending();

        Assert.assertTrue(dashboardPage.areAllValidatorsDisplayed(), "Validator 1, 2, and 3 should be displayed");
        Assert.assertTrue(dashboardPage.arePendingSubtablesDisplayed(), "Lembur, Cuti, and Koreksi subtables should be displayed");
        Assert.assertTrue(dashboardPage.arePendingTableHeadersDisplayed(), "NAMA and TOTAL headers should be displayed");
    }

    @Test(description = "TC-ADM-DSH-PND-002: Verify Pending date range filter with valid Start Date and End Date")
    public void testTC_ADM_DSH_PND_002_PendingFilterByValidDateRange() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openPending();

        dashboardPage.filterPendingByDate("2026-11-01", "2026-11-17");
        Assert.assertTrue(dashboardPage.areAllValidatorsDisplayed(), "Validators should update and remain displayed after date filter");
    }

    @Test(description = "TC-ADM-DSH-PND-003: Verify Reset button on Pending page restores default state")
    public void testTC_ADM_DSH_PND_003_PendingResetFilter() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openPending();

        dashboardPage.filterPendingByDate("2026-11-01", "2026-11-17");
        dashboardPage.clickResetPendingFilter();
        Assert.assertTrue(dashboardPage.areAllValidatorsDisplayed(), "Pending queues should reload to default after Reset");
    }

    @Test(description = "TC-ADM-DSH-PND-004 & 005: Verify clicking employee in Pending Lembur navigates to v1 detail without Approve/Reject buttons")
    public void testTC_ADM_DSH_PND_004_005_PendingLemburDetailAndNoApproveReject() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openPending();

        boolean navigated = dashboardPage.clickFirstEmployeeByRouteFragment("v1-lembur");
        if (navigated) {
            Assert.assertTrue(dashboardPage.getCurrentUrl().contains("v1-lembur"), "Should navigate to v1-lembur detail page");
            Assert.assertFalse(dashboardPage.hasApproveOrRejectButtonsOnDetail(),
                    "TC-ADM-DSH-PND-005: Actual detail page should not expose Approve/Reject buttons for admin");
        } else {
            Assert.assertTrue(dashboardPage.areAllValidatorsDisplayed(), "Pending page healthy when no pending lembur rows exist");
        }
    }

    @Test(description = "TC-ADM-DSH-PND-006: Verify clicking employee in Pending Cuti navigates to v1 cuti detail")
    public void testTC_ADM_DSH_PND_006_PendingCutiDetailNavigation() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openPending();

        boolean navigated = dashboardPage.clickFirstEmployeeByRouteFragment("v1-cuti");
        if (navigated) {
            Assert.assertTrue(dashboardPage.getCurrentUrl().contains("v1-cuti"), "Should navigate to v1-cuti detail page");
        } else {
            Assert.assertTrue(dashboardPage.areAllValidatorsDisplayed(), "Pending page healthy when no pending cuti rows exist");
        }
    }

    @Test(description = "TC-ADM-DSH-PND-007: Verify clicking employee in Pending Koreksi navigates to koreksi detail")
    public void testTC_ADM_DSH_PND_007_PendingKoreksiDetailNavigation() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openPending();

        boolean navigated = dashboardPage.clickFirstEmployeeByRouteFragment("koreksi");
        if (navigated) {
            Assert.assertTrue(dashboardPage.getCurrentUrl().contains("koreksi"), "Should navigate to koreksi detail page");
        } else {
            Assert.assertTrue(dashboardPage.areAllValidatorsDisplayed(), "Pending page healthy when no pending koreksi rows exist");
        }
    }

    @Test(description = "TC-ADM-DSH-PND-008: Negative test - Pending reversed date range (End Date earlier than Start Date) handles gracefully")
    public void testTC_ADM_DSH_PND_008_PendingReversedDateRangeNegative() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.openPending();

        dashboardPage.filterPendingByDate("2026-11-20", "2026-11-10");
        Assert.assertTrue(dashboardPage.areAllValidatorsDisplayed(),
                "System must not crash (no 500 unhandled error) on reversed date range");
    }
}
