package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * VisibilityPage - Verify visibility states of elements.
 */
public class VisibilityPage extends BasePage {

    private final By removedButton = By.id("removedButton");
    private final By zeroWidthButton = By.id("zeroWidthButton");
    private final By overlappedButton = By.id("overlappedButton");
    private final By transparentButton = By.id("transparentButton");
    private final By hiddenButton = By.id("hiddenButton");
    private final By notDisplayedButton = By.id("notDisplayedButton");
    private final By offscreenButton = By.id("offscreenButton");
    private final By visibleButton = By.id("visibleButton");
    private final By hideActionButton = By.xpath("//button[contains(text(),'Hide')]");

    public VisibilityPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToVisibilityPage() {
        navigateToPage("/visibility");
    }

    public void clickHide() {
        try {
            WaitUtils.waitForClickable(driver, hideActionButton, 5).click();
        } catch (Exception e) {
            logger.warn("Hide button not found: {}", e.getMessage());
        }
    }

    public boolean isRemovedButtonPresent() {
        return WaitUtils.isElementPresent(driver, removedButton);
    }

    public boolean isHiddenButtonVisible() {
        try {
            WebElement btn = driver.findElement(hiddenButton);
            return btn.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isVisibleButtonDisplayed() {
        return isDisplayed(visibleButton);
    }

    public boolean isTransparentButtonInDom() {
        return WaitUtils.isElementPresent(driver, transparentButton);
    }

    public boolean isOffscreenButtonInDom() {
        return WaitUtils.isElementPresent(driver, offscreenButton);
    }

    public boolean isZeroWidthButtonInDom() {
        return WaitUtils.isElementPresent(driver, zeroWidthButton);
    }

    public boolean isNotDisplayedButtonInDom() {
        return WaitUtils.isElementPresent(driver, notDisplayedButton);
    }

    public boolean isOverlappedButtonInDom() {
        return WaitUtils.isElementPresent(driver, overlappedButton);
    }

    public boolean isPageLoaded() {
        return isDisplayed(visibleButton);
    }
}
