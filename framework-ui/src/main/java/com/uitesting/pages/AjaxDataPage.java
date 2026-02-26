package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * AjaxDataPage - Elements appear after AJAX request completes.
 * Must wait explicitly for elements to appear, not use fixed sleeps.
 */
public class AjaxDataPage extends BasePage {

    // AJAX button triggers the data load
    private final By ajaxButton = By.cssSelector("button.btn-primary");

    // Result label appears after AJAX call completes (may take several seconds)
    // ...existing code...
    private final By resultContent = By.cssSelector(".bg-success");
    private final By loadingIndicator = By.cssSelector(".spinner-border");

    public AjaxDataPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToAjaxDataPage() {
        navigateToPage("/ajax");
    }

    public void clickAjaxButton() {
        logger.info("Clicking AJAX button to trigger data load");
        WaitUtils.waitForClickable(driver, ajaxButton).click();
    }

    /**
     * Wait for AJAX response to complete and content to appear
     */
    public void waitForAjaxContent() {
        logger.info("Waiting for AJAX content to load");
        WaitUtils.waitForVisible(driver, resultContent, 20);
    }

    public boolean isAjaxContentDisplayed() {
        try {
            WaitUtils.waitForVisible(driver, resultContent, 20);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getAjaxResultText() {
        return getText(resultContent);
    }

    public boolean isLoadingIndicatorVisible() {
        return isDisplayed(loadingIndicator);
    }

    /**
     * Click button and wait for result - complete AJAX flow
     */
    public String clickAndWaitForResult() {
        clickAjaxButton();
        waitForAjaxContent();
        return getAjaxResultText();
    }

    public boolean isPageLoaded() {
        return isDisplayed(ajaxButton);
    }
}
