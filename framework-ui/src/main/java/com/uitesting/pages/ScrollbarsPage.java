package com.uitesting.pages;

import com.uitesting.utils.JSUtils;
import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * ScrollbarsPage - Scrolling an element into view before interaction.
 */
public class ScrollbarsPage extends BasePage {

    private final By scrollableButton = By.id("hidingButton");

    public ScrollbarsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToScrollbarsPage() {
        navigateToPage("/scrollbars");
    }

    /**
     * Scroll the button into view using JavaScript, then click it
     */
    public void scrollToButtonAndClick() {
        logger.info("Scrolling button into view and clicking");
        WebElement btn = WaitUtils.waitForPresence(driver, scrollableButton);
        JSUtils.scrollToElement(driver, btn);
        WaitUtils.waitForClickable(driver, scrollableButton).click();
    }

    /**
     * Verify button is visible in the viewport after scrolling
     */
    public boolean isButtonInViewport() {
        WebElement btn = driver.findElement(scrollableButton);
        JSUtils.scrollToElement(driver, btn);
        return JSUtils.isElementInViewport(driver, btn);
    }

    public boolean isButtonDisplayed() {
        return WaitUtils.isElementPresent(driver, scrollableButton);
    }

    public boolean isPageLoaded() {
        return isDisplayed(By.tagName("body"));
    }

    /**
     * Scroll to top of page
     */
    public void scrollToTop() {
        JSUtils.scrollToTop(driver);
    }

    /**
     * Scroll to bottom of page
     */
    public void scrollToBottom() {
        JSUtils.scrollToBottom(driver);
    }
}
