package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * DynamicIdPage - Tests that element location does not rely on dynamic IDs.
 * Use stable selectors: CSS class, text, name, or XPath with text()
 */
public class DynamicIdPage extends BasePage {

    // ✅ CORRECT: Using class-based selector, NOT the dynamic ID
    @FindBy(css = "button.btn-primary")
    private WebElement dynamicButton;

    // ✅ Also valid: XPath using text content, not ID
    private final By buttonByText = By.xpath("//button[contains(@class,'btn-primary')]");
    private final By pageTitle = By.cssSelector("h3");

    public DynamicIdPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToDynamicIdPage() {
        navigateToPage("/dynamicid");
    }

    /**
     * Click button using stable class selector (NOT its dynamic ID)
     */
    public void clickDynamicButton() {
        logger.info("Clicking dynamic button using class-based selector");
        WaitUtils.waitForClickable(driver, buttonByText).click();
    }

    public boolean isDynamicButtonDisplayed() {
        return isDisplayed(buttonByText);
    }

    /**
     * Verify button does NOT use ID for identification
     * The button element has a dynamic ID that changes on each page load
     */
    public boolean doesButtonHaveDynamicId() {
        WebElement btn = driver.findElement(buttonByText);
        String id = btn.getAttribute("id");
        return id != null && !id.isEmpty();
    }

    public String getButtonId() {
        return driver.findElement(buttonByText).getAttribute("id");
    }

    public String getPageHeadingText() {
        return getText(pageTitle);
    }

    public boolean isPageLoaded() {
        return isDisplayed(By.cssSelector("h3")) && isDisplayed(buttonByText);
    }
}
