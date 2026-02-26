package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
// ...existing code...

/**
 * ClassAttributePage - Tests that class-based XPath is well-formed.
 * Buttons may have multiple classes; XPath must use contains() not exact match.
 */
public class ClassAttributePage extends BasePage {

    // ✅ CORRECT: contains() handles multi-class attributes
    private final By primaryButtonByClass = By.xpath("//button[contains(@class,'btn-primary')]");

    // ✅ CSS equivalent - also correct
    private final By primaryButtonByCss = By.cssSelector("button.btn-primary");

    // ❌ INCORRECT approach (demonstrates the pitfall) - exact class match fails with multiple classes
    // By.xpath("//button[@class='btn btn-primary']") - would fail if extra classes present

    // ...existing code...

    public ClassAttributePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToClassAttributePage() {
        navigateToPage("/classattr");
    }

    /**
     * Click the primary button using well-formed XPath with contains()
     */
    public void clickPrimaryButton() {
        logger.info("Clicking primary button using contains() XPath");
        WaitUtils.waitForClickable(driver, primaryButtonByClass).click();
    }

    public void clickPrimaryButtonByCss() {
        logger.info("Clicking primary button using CSS class selector");
        WaitUtils.waitForClickable(driver, primaryButtonByCss).click();
    }

    public boolean isPrimaryButtonDisplayed() {
        return isDisplayed(primaryButtonByClass);
    }

    /**
     * Verify the button has multiple classes (validates test approach is needed)
     */
    public String getButtonClassAttribute() {
        return driver.findElement(primaryButtonByClass).getAttribute("class");
    }

    /**
     * Verify page loaded with expected elements
     */
    public boolean isPageLoaded() {
        return isDisplayed(primaryButtonByClass);
    }

    /**
     * Returns the alert text after clicking the button
     */
    public String clickAndGetAlertText() {
        clickPrimaryButton();
        try {
            WaitUtils.hardSleep(500);
            String alertText = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept();
            return alertText;
        } catch (Exception e) {
            logger.warn("No alert appeared after clicking primary button");
            return "";
        }
    }
}
