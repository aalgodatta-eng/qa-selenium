package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * DisabledInputPage - Wait for input field to become enabled before interacting.
 */
public class DisabledInputPage extends BasePage {

    private final By enableButton = By.id("enableButton");
    private final By inputField = By.id("inputField");

    public DisabledInputPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToDisabledInputPage() {
        navigateToPage("/disabledinput");
    }

    public void clickEnableButton() {
        logger.info("Clicking enable button");
        WaitUtils.waitForClickable(driver, enableButton).click();
    }

    /**
     * Wait for the input to become enabled (not just present)
     */
    public void waitForInputToBeEnabled() {
        logger.info("Waiting for input field to become enabled");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(inputField));
    }

    /**
     * Full flow: click enable and wait for field to be enabled
     */
    public void enableAndWaitForInput() {
        clickEnableButton();
        waitForInputToBeEnabled();
    }

    public void enterTextIntoInput(String text) {
        logger.info("Entering text: {}", text);
        WebElement input = WaitUtils.waitForClickable(driver, inputField);
        input.clear();
        input.sendKeys(text);
    }

    public boolean isInputEnabled() {
        try {
            WebElement input = driver.findElement(inputField);
            return input.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInputDisabled() {
        return !isInputEnabled();
    }

    public String getInputValue() {
        return driver.findElement(inputField).getAttribute("value");
    }

    /**
     * Attempt to type in disabled field (NEGATIVE test)
     */
    public boolean tryTypingInDisabledField(String text) {
        try {
            WebElement input = driver.findElement(inputField);
            if (!input.isEnabled()) {
                input.sendKeys(text);
                return false; // Should not reach here
            }
            return false;
        } catch (Exception e) {
            return false; // Expected - field is disabled
        }
    }

    public boolean isPageLoaded() {
        return isDisplayed(enableButton) && isDisplayed(inputField);
    }
}
