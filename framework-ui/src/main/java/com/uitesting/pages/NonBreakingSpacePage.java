package com.uitesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * NonBreakingSpacePage - Non-breaking spaces (\u00A0) look like normal spaces on screen
 * but are different characters. XPath with regular space will fail.
 */
public class NonBreakingSpacePage extends BasePage {

    // The button has non-breaking space in its text
    // Must use normalize-space() or \u00A0 in XPath
    private final By pageContent = By.cssSelector(".container");

    public NonBreakingSpacePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToNonBreakingSpacePage() {
        navigateToPage("/nbsp");
    }

    /**
     * Click button using normalize-space() - handles both regular and non-breaking spaces
     */
    public void clickButtonByNormalizedText(String buttonText) {
        logger.info("Clicking button with normalize-space() XPath: '{}'", buttonText);
        By locator = By.xpath("//button[normalize-space()='" + buttonText + "']");
        waitAndClick(locator);
    }

    /**
     * Click button using Unicode non-breaking space character (\u00A0)
     */
    public void clickButtonWithNbsp(String textWithNbsp) {
        logger.info("Clicking button with non-breaking space in XPath");
        // Replace visible space with Unicode NBSP (\u00A0)
        String nbspText = textWithNbsp.replace(" ", "\u00A0");
        By locator = By.xpath("//button[text()='" + nbspText + "']");
        waitAndClick(locator);
    }

    /**
     * Check if the text in an element contains non-breaking space
     */
    public boolean textContainsNbsp(By locator) {
        WebElement element = driver.findElement(locator);
        String text = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].textContent;", element);
        return text.contains("\u00A0");
    }

    /**
     * Try to find button with regular space (NEGATIVE - will fail)
     */
    public boolean findButtonWithRegularSpace(String text) {
        try {
            driver.findElement(By.xpath("//button[text()='" + text + "']"));
            return true;
        } catch (Exception e) {
            return false; // Expected: fails with non-breaking space
        }
    }

    /**
     * Find button using contains() - works regardless of space type
     */
    public boolean findButtonWithContains(String partialText) {
        try {
            driver.findElement(By.xpath("//button[contains(.,'" + partialText + "')]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPageLoaded() {
        return isDisplayed(pageContent);
    }
}
