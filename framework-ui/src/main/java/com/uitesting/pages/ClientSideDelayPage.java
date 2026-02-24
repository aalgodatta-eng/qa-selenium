package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * ClientSideDelayPage - Element appears after client-side JavaScript calculation.
 * Must use explicit waits, not Thread.sleep().
 */
public class ClientSideDelayPage extends BasePage {

    private final By generateButton = By.id("ajaxButton");
    private final By resultButton = By.id("resultButton");
    private final By loadingContainer = By.id("spinner");

    public ClientSideDelayPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToClientSideDelayPage() {
        navigateToPage("/clientdelay");
    }

    public void clickGenerateButton() {
        logger.info("Clicking generate button (triggers client-side delay)");
        WaitUtils.waitForClickable(driver, generateButton).click();
    }

    /**
     * Wait for result button to appear after JS calculation completes
     * Client-side delay can be up to 15 seconds
     */
    public void waitForResultButton() {
        logger.info("Waiting for result button after client-side delay");
        WaitUtils.waitForVisible(driver, resultButton, 25);
    }

    public boolean isResultButtonDisplayed() {
        try {
            WaitUtils.waitForVisible(driver, resultButton, 25);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickResultButton() {
        WaitUtils.waitForClickable(driver, resultButton, 25).click();
    }

    /**
     * Complete flow: click generate, wait, then click result
     */
    public void performCompleteFlow() {
        clickGenerateButton();
        waitForResultButton();
        clickResultButton();
    }

    public boolean isPageLoaded() {
        return isDisplayed(generateButton);
    }

    public boolean isLoadingSpinnerGone() {
        try {
            WaitUtils.waitForInvisibility(driver, loadingContainer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
