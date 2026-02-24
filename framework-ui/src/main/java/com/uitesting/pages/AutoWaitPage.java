package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * AutoWaitPage - Wait for element to become interactable before performing action.
 */
public class AutoWaitPage extends BasePage {

    private final By applyButton = By.id("applyButton");
    private final By resultLabel = By.id("result");
    private final By targetButton = By.id("target");

    public AutoWaitPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToAutoWaitPage() {
        navigateToPage("/autowait");
    }

    public void clickApplyButton() {
        logger.info("Clicking Apply button");
        WaitUtils.waitForClickable(driver, applyButton).click();
    }

    /**
     * Wait for target to become interactable and then click
     */
    public void waitForTargetAndClick() {
        logger.info("Waiting for target button to become interactable");
        WaitUtils.waitForClickable(driver, targetButton, 20).click();
    }

    public String getResultText() {
        try {
            return WaitUtils.waitForVisible(driver, resultLabel, 10).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isTargetButtonDisplayed() {
        return isDisplayed(targetButton);
    }

    public boolean isPageLoaded() {
        return isDisplayed(applyButton);
    }
}
