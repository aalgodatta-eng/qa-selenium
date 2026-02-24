package com.uitesting.pages;

import com.uitesting.utils.JSUtils;
import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * HiddenLayersPage - Tests that interaction only happens with visible elements.
 * After clicking the green button, a blue button appears over it (z-order).
 * Clicking an element covered by another (blue) should not work.
 */
public class HiddenLayersPage extends BasePage {

    // Green button is the actual clickable target
    private final By greenButton = By.cssSelector("#greenButton");

    // Blue button appears on top after clicking green (hidden layer / z-order overlay)
    private final By blueButton = By.cssSelector("#blueButton");

    public HiddenLayersPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToHiddenLayersPage() {
        navigateToPage("/hiddenlayers");
    }

    public void clickGreenButton() {
        logger.info("Clicking green button");
        WaitUtils.waitForClickable(driver, greenButton).click();
    }

    public boolean isGreenButtonDisplayed() {
        return isDisplayed(greenButton);
    }

    public boolean isBlueButtonVisible() {
        try {
            WebElement blue = driver.findElement(blueButton);
            return blue.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * After clicking green, verify green is no longer clickable (covered by blue)
     */
    public boolean isGreenButtonClickable() {
        try {
            WaitUtils.waitForClickable(driver, greenButton, 3);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify we do NOT interact with hidden/covered elements.
     * Green button becomes non-interactive once blue button overlays it.
     */
    public boolean isBlueButtonOverGreen() {
        try {
            WebElement green = driver.findElement(greenButton);
            WebElement blue = driver.findElement(blueButton);
            int greenZIndex = Integer.parseInt(
                green.getCssValue("z-index").equals("auto") ? "0" : green.getCssValue("z-index"));
            int blueZIndex = Integer.parseInt(
                blue.getCssValue("z-index").equals("auto") ? "0" : blue.getCssValue("z-index"));
            return blueZIndex > greenZIndex;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if an element is truly visible (not hidden by another layer via JS)
     */
    public boolean isElementTrulyInteractable(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return (Boolean) JSUtils.executeScript(driver,
                "var el = arguments[0]; " +
                "var rect = el.getBoundingClientRect(); " +
                "var topEl = document.elementFromPoint(rect.left + rect.width/2, rect.top + rect.height/2); " +
                "return el === topEl || el.contains(topEl);", element);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPageLoaded() {
        return isDisplayed(greenButton);
    }
}
