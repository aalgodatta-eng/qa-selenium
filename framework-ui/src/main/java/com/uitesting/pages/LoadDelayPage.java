package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoadDelayPage - Tests that automation can handle delayed page loading.
 * The button appears after 5 seconds of page load.
 */
public class LoadDelayPage extends BasePage {

    // Button only appears after page finishes loading (delayed ~5 sec)
    private final By loadedButton = By.cssSelector("button.btn-primary");
    private final By loadingIndicator = By.id("loadingImage");

    public LoadDelayPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLoadDelayPage() {
        navigateToPage("/loaddelay");
    }

    /**
     * Wait for the button to appear after page load completes
     */
    public void waitForButtonToAppear() {
        logger.info("Waiting for delayed button to appear (up to 15 seconds)");
        WaitUtils.waitForVisible(driver, loadedButton, 15);
    }

    public void clickLoadedButton() {
        waitForButtonToAppear();
        WaitUtils.waitForClickable(driver, loadedButton).click();
    }

    public boolean isButtonDisplayed() {
        try {
            return WaitUtils.waitForVisible(driver, loadedButton, 15).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoadingIndicatorPresent() {
        return WaitUtils.isElementPresent(driver, loadingIndicator);
    }

    /**
     * Verify page loads correctly within the expected time window
     */
    public boolean isPageFullyLoaded() {
        return isDisplayed(loadedButton);
    }

    public boolean isPageLoaded() {
        try {
            WaitUtils.waitForVisible(driver, loadedButton, 15);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
