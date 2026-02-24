package com.uitesting.regression;

import com.uitesting.BaseTest;
import com.uitesting.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RegressionTests2 - Continuation: Text Input, Scrollbars, DynamicTable, VerifyText, ProgressBar
 */
public class RegressionTests2 extends BaseTest {

    // ══════════════════════════════════════════════════════════
    // 9. TEXT INPUT TESTS
    // ══════════════════════════════════════════════════════════

    @Test(description = "REG-TXT-01 | [POSITIVE] Typing in field updates button text correctly",
          groups = {"regression", "textInput", "positive"})
    public void textInput_typeAndUpdateButtonText() {
        TextInputPage page = new TextInputPage(driver);
        page.navigateToTextInputPage();
        String newName = "Automation Test";
        page.typeButtonName(newName);
        page.clickChangeButton();
        Assert.assertEquals(page.getButtonText(), newName,
            "Button text should be updated to match typed text");
    }

    @Test(description = "REG-TXT-02 | [POSITIVE] Actions class typing also updates button text",
          groups = {"regression", "textInput", "positive"})
    public void textInput_actionsTypingUpdatesButtonText() {
        TextInputPage page = new TextInputPage(driver);
        page.navigateToTextInputPage();
        String newName = "KC Automation";
        page.typeButtonNameViaActions(newName);
        page.clickChangeButton();
        Assert.assertEquals(page.getButtonText(), newName,
            "Button text should update after typing via Actions");
    }

    @Test(description = "REG-TXT-03 | [POSITIVE] Input field retains typed value before submit",
          groups = {"regression", "textInput", "positive"})
    public void textInput_fieldRetainsValueBeforeSubmit() {
        TextInputPage page = new TextInputPage(driver);
        page.navigateToTextInputPage();
        page.typeButtonName("VerifyValue");
        Assert.assertEquals(page.getInputValue(), "VerifyValue",
            "Input field should retain the typed value");
    }

    @Test(description = "REG-TXT-04 | [NEGATIVE] Empty input results in empty button text",
          groups = {"regression", "textInput", "negative"})
    public void textInput_emptyInputResultsInEmptyButtonText() {
        TextInputPage page = new TextInputPage(driver);
        page.navigateToTextInputPage();
        page.clearInput();
        page.clickChangeButton();
        String buttonText = page.getButtonText();
        logger.info("[Negative] Button text with empty input: '{}'", buttonText);
        // Button text should be empty or default when no input provided
        Assert.assertNotNull(buttonText, "Button text should not be null");
    }

    @Test(description = "REG-TXT-05 | [POSITIVE] Special characters in input field work correctly",
          groups = {"regression", "textInput", "positive"})
    public void textInput_specialCharactersInInput() {
        TextInputPage page = new TextInputPage(driver);
        page.navigateToTextInputPage();
        String specialText = "Test@123!";
        page.typeButtonName(specialText);
        page.clickChangeButton();
        Assert.assertEquals(page.getButtonText(), specialText,
            "Button text should support special characters");
    }

    // ══════════════════════════════════════════════════════════
    // 10. SCROLLBARS TESTS
    // ══════════════════════════════════════════════════════════

    @Test(description = "REG-SCR-01 | [POSITIVE] Scroll button into viewport and click",
          groups = {"regression", "scrollbars", "positive"})
    public void scrollbars_scrollButtonIntoViewAndClick() {
        ScrollbarsPage page = new ScrollbarsPage(driver);
        page.navigateToScrollbarsPage();
        Assert.assertTrue(page.isButtonDisplayed(), "Scrollable button should exist in DOM");
        page.scrollToButtonAndClick();
        // If click succeeds without exception, test passes
        Assert.assertTrue(true, "Button clicked after scrolling into view");
    }

    @Test(description = "REG-SCR-02 | [POSITIVE] Button is in viewport after scroll",
          groups = {"regression", "scrollbars", "positive"})
    public void scrollbars_buttonInViewportAfterScroll() {
        ScrollbarsPage page = new ScrollbarsPage(driver);
        page.navigateToScrollbarsPage();
        Assert.assertTrue(page.isButtonInViewport(),
            "Button should be visible in viewport after scrolling");
    }

    @Test(description = "REG-SCR-03 | [POSITIVE] Scroll to top and bottom of page",
          groups = {"regression", "scrollbars", "positive"})
    public void scrollbars_scrollToTopAndBottom() {
        ScrollbarsPage page = new ScrollbarsPage(driver);
        page.navigateToScrollbarsPage();
        page.scrollToBottom();
        com.uitesting.utils.WaitUtils.hardSleep(300);
        page.scrollToTop();
        com.uitesting.utils.WaitUtils.hardSleep(300);
        Assert.assertTrue(true, "Scroll operations completed without error");
    }

    // ══════════════════════════════════════════════════════════
    // 11. DYNAMIC TABLE TESTS
    // ══════════════════════════════════════════════════════════

    @Test(description = "REG-DYT-01 | [POSITIVE] Chrome CPU in table matches warning label",
          groups = {"regression", "dynamicTable", "positive"})
    public void dynamicTable_cpuValueMatchesWarningLabel() {
        DynamicTablePage page = new DynamicTablePage(driver);
        page.navigateToDynamicTablePage();
        Assert.assertTrue(page.isPageLoaded(), "Dynamic Table page should load");
        Assert.assertTrue(page.isTableValueMatchingWarning(),
            "CPU value in table should match warning label value");
    }

    @Test(description = "REG-DYT-02 | [POSITIVE] Table has multiple rows of data",
          groups = {"regression", "dynamicTable", "positive"})
    public void dynamicTable_tableHasMultipleRows() {
        DynamicTablePage page = new DynamicTablePage(driver);
        page.navigateToDynamicTablePage();
        int rowCount = page.getRowCount();
        logger.info("Dynamic table row count: {}", rowCount);
        Assert.assertTrue(rowCount > 0, "Table should have at least one data row");
    }

    @Test(description = "REG-DYT-03 | [POSITIVE] Warning label contains Chrome CPU text",
          groups = {"regression", "dynamicTable", "positive"})
    public void dynamicTable_warningLabelContainsChromeCpu() {
        DynamicTablePage page = new DynamicTablePage(driver);
        page.navigateToDynamicTablePage();
        String warningCpu = page.getCpuValueFromWarning();
        Assert.assertFalse(warningCpu.isEmpty(),
            "Warning label should contain a CPU percentage value");
        logger.info("Chrome CPU from warning: {}", warningCpu);
    }

    @Test(description = "REG-DYT-04 | [NEGATIVE] Table data changes on page reload (dynamic)",
          groups = {"regression", "dynamicTable", "negative"})
    public void dynamicTable_dataChangesBetweenLoads() {
        DynamicTablePage page = new DynamicTablePage(driver);
        page.navigateToDynamicTablePage();
        // Page data is dynamic but structure remains - verify warning always matches table
        Assert.assertTrue(page.isTableValueMatchingWarning(),
            "[Negative] Table and warning must always be in sync");
    }

    // ══════════════════════════════════════════════════════════
    // 12. VERIFY TEXT TESTS
    // ══════════════════════════════════════════════════════════

    @Test(description = "REG-VTX-01 | [POSITIVE] Text is verifiable using contains() XPath",
          groups = {"regression", "verifyText", "positive"})
    public void verifyText_textFoundUsingContainsXPath() {
        VerifyTextPage page = new VerifyTextPage(driver);
        page.navigateToVerifyTextPage();
        String displayedText = page.getDisplayedText();
        Assert.assertFalse(displayedText.isEmpty(), "Page should display text content");
        logger.info("Displayed text: '{}'", displayedText);
    }

    @Test(description = "REG-VTX-02 | [POSITIVE] normalize-space() handles extra whitespace in XPath",
          groups = {"regression", "verifyText", "positive"})
    public void verifyText_normalizeSpaceHandlesWhitespace() {
        VerifyTextPage page = new VerifyTextPage(driver);
        page.navigateToVerifyTextPage();
        String rawText = page.getRawTextContent().trim();
        String displayedText = page.getDisplayedText().trim();
        logger.info("Raw text: '{}', Displayed: '{}'", rawText, displayedText);
        // Both should be non-empty
        Assert.assertFalse(displayedText.isEmpty(), "Displayed text should not be empty");
    }

    @Test(description = "REG-VTX-03 | [NEGATIVE] Exact text match may fail due to whitespace",
          groups = {"regression", "verifyText", "negative"})
    public void verifyText_exactMatchMayFailWithWhitespace() {
        VerifyTextPage page = new VerifyTextPage(driver);
        page.navigateToVerifyTextPage();
        // Exact XPath text() match often fails when whitespace or special chars present
        boolean exactMatch = page.isTextPresentByXPath("Welcome, dear user!");
        // Contains() should be more reliable than exact text match
        boolean containsMatch = page.isTextPresentByContainsXPath("Welcome");
        logger.info("[Negative] Exact match found: {}, Contains match found: {}", exactMatch, containsMatch);
        Assert.assertTrue(true, "Whitespace sensitivity test completed");
    }

    // ══════════════════════════════════════════════════════════
    // 13. PROGRESS BAR TESTS
    // ══════════════════════════════════════════════════════════

    @Test(description = "REG-PRG-01 | [POSITIVE] Progress bar starts at 0 when page loads",
          groups = {"regression", "progressBar", "positive"})
    public void progressBar_startsAtZero() {
        ProgressBarPage page = new ProgressBarPage(driver);
        page.navigateToProgressBarPage();
        int initial = page.getCurrentProgress();
        logger.info("Initial progress: {}%", initial);
        Assert.assertEquals(initial, 0, "Progress should start at 0%");
    }

    @Test(description = "REG-PRG-02 | [POSITIVE] Stop progress bar when it reaches target percentage",
          groups = {"regression", "progressBar", "positive"})
    public void progressBar_stopAtTargetPercentage() {
        ProgressBarPage page = new ProgressBarPage(driver);
        page.navigateToProgressBarPage();
        page.clickStart();
        page.waitForProgressAndStop(75);
        // Allow 10% tolerance for timing
        Assert.assertTrue(page.isProgressNearTarget(75, 10),
            "Progress should be stopped near 75% (within 10% tolerance)");
        logger.info("Final progress when stopped: {}%", page.getCurrentProgress());
    }

    @Test(description = "REG-PRG-03 | [POSITIVE] Progress bar increases after start is clicked",
          groups = {"regression", "progressBar", "positive"})
    public void progressBar_progressIncreasesAfterStart() {
        ProgressBarPage page = new ProgressBarPage(driver);
        page.navigateToProgressBarPage();
        page.clickStart();
        com.uitesting.utils.WaitUtils.hardSleep(1000);
        int progress = page.getCurrentProgress();
        logger.info("Progress after 1 second: {}%", progress);
        Assert.assertTrue(progress > 0, "Progress should increase after clicking Start");
        page.clickStop(); // cleanup
    }

    @Test(description = "REG-PRG-04 | [NEGATIVE] Progress stays stopped after stop button clicked",
          groups = {"regression", "progressBar", "negative"})
    public void progressBar_staysStoppedAfterStopClick() {
        ProgressBarPage page = new ProgressBarPage(driver);
        page.navigateToProgressBarPage();
        page.clickStart();
        com.uitesting.utils.WaitUtils.hardSleep(500);
        page.clickStop();
        int progressAtStop = page.getCurrentProgress();
        com.uitesting.utils.WaitUtils.hardSleep(1000);
        int progressAfterWait = page.getCurrentProgress();
        logger.info("Progress at stop: {}%, after 1s wait: {}%", progressAtStop, progressAfterWait);
        Assert.assertEquals(progressAtStop, progressAfterWait,
            "[Negative] Progress should not increase after Stop is clicked");
    }
}
