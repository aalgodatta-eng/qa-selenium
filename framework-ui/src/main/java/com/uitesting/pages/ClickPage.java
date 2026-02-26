package com.uitesting.pages;

import com.uitesting.utils.JSUtils;
import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * ClickPage - Event-based click may not always work.
 * Must use WebDriver click (not JS click) to fire event handlers correctly.
 */
public class ClickPage extends BasePage {

    private final By clickButton = By.id("badButton");
    // ...existing code...

    public ClickPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToClickPage() {
        navigateToPage("/click");
    }

    /**
     * Standard Selenium click - triggers event listeners correctly
     */
    public void clickButton() {
        logger.info("Clicking button using standard Selenium click");
        WaitUtils.waitForClickable(driver, clickButton).click();
    }

    /**
     * Actions-based click - more reliable for event-based buttons
     */
    public void clickButtonViaActions() {
        logger.info("Clicking button via Actions class");
        WebElement btn = WaitUtils.waitForVisible(driver, clickButton);
        new Actions(driver).click(btn).perform();
    }

    /**
     * JavaScript click - may NOT trigger event listeners (use as last resort)
     * This demonstrates the negative case - JS click may fail on event-based buttons
     */
    public void clickButtonViaJS() {
        logger.info("Clicking button via JavaScript (may not fire event handlers)");
        WebElement btn = WaitUtils.waitForVisible(driver, clickButton);
        JSUtils.clickViaJS(driver, btn);
    }

    public boolean isButtonGreen() {
        try {
            WebElement btn = driver.findElement(clickButton);
            String classes = btn.getAttribute("class");
            return classes != null && classes.contains("btn-success");
        } catch (Exception e) {
            return false;
        }
    }

    public String getButtonClass() {
        return driver.findElement(clickButton).getAttribute("class");
    }

    public boolean isPageLoaded() {
        return isDisplayed(clickButton);
    }
}
