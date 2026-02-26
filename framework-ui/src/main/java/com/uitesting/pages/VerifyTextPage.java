package com.uitesting.pages;

import com.uitesting.utils.JSUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * VerifyTextPage - Finding element by text has nuances.
 * Text may include non-breaking spaces, extra whitespace, or be in child elements.
 */
public class VerifyTextPage extends BasePage {

    private final By textContainer = By.cssSelector(".bg-primary");
    // ...existing code...

    public VerifyTextPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToVerifyTextPage() {
        navigateToPage("/verifytext");
    }

    /**
     * Get text using Selenium getText() - trims whitespace automatically
     */
    public String getDisplayedText() {
        WebElement el = driver.findElement(textContainer);
        return el.getText();
    }

    /**
     * Get raw text using JavaScript - includes all whitespace/chars
     */
    public String getRawTextContent() {
        WebElement el = driver.findElement(textContainer);
        return JSUtils.getTextViaJS(driver, el);
    }

    /**
     * Verify text using normalized comparison (handles whitespace nuances)
     */
    public boolean verifyTextEquals(String expectedText) {
        String actual = getDisplayedText().trim();
        String expected = expectedText.trim();
        logger.info("Verifying text: expected='{}', actual='{}'", expected, actual);
        return actual.equals(expected);
    }

    /**
     * Verify text using contains (more lenient)
     */
    public boolean verifyTextContains(String expectedText) {
        String actual = getDisplayedText();
        return actual.contains(expectedText);
    }

    /**
     * XPath with text() - exact text match (may fail with extra whitespace)
     */
    public boolean isTextPresentByXPath(String text) {
        try {
            driver.findElement(By.xpath("//*[text()='" + text + "']"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * XPath with normalize-space() - handles extra whitespace
     */
    public boolean isTextPresentByNormalizedXPath(String text) {
        try {
            driver.findElement(By.xpath("//*[normalize-space()='" + text + "']"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * XPath with contains() - most flexible
     */
    public boolean isTextPresentByContainsXPath(String text) {
        try {
            driver.findElement(By.xpath("//*[contains(text(),'" + text + "')]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPageLoaded() {
        return isDisplayed(textContainer);
    }
}
