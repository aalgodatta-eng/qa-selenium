package com.uitesting.pages;

// ...existing code...
import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * TextInputPage - Text input may not work via simple sendKeys.
 * Must use WebDriver's sendKeys with proper element interaction.
 */
public class TextInputPage extends BasePage {

    private final By inputField = By.id("newButtonName");
    private final By changeButton = By.id("updatingButton");

    public TextInputPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToTextInputPage() {
        navigateToPage("/textinput");
    }

    /**
     * Type into field using click-first then sendKeys
     * (Direct sendKeys sometimes fails if element doesn't have focus)
     */
    public void typeButtonName(String name) {
        logger.info("Typing '{}' into text input", name);
        WebElement input = WaitUtils.waitForClickable(driver, inputField);
        input.click();
        input.clear();
        input.sendKeys(name);
    }

    /**
     * Type using Actions class - more reliable for complex inputs
     */
    public void typeButtonNameViaActions(String name) {
        logger.info("Typing '{}' via Actions class", name);
        WebElement input = WaitUtils.waitForVisible(driver, inputField);
        new Actions(driver)
            .click(input)
            .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
            .sendKeys(Keys.DELETE)
            .sendKeys(name)
            .perform();
    }

    public void clickChangeButton() {
        WaitUtils.waitForClickable(driver, changeButton).click();
    }

    public String getButtonText() {
        return driver.findElement(changeButton).getText();
    }

    public String getInputValue() {
        return driver.findElement(inputField).getAttribute("value");
    }

    public boolean isButtonTextUpdated(String expectedText) {
        return getButtonText().equals(expectedText);
    }

    public void clearInput() {
        WebElement input = WaitUtils.waitForClickable(driver, inputField);
        input.click();
        input.clear();
    }

    public boolean isPageLoaded() {
        return isDisplayed(inputField) && isDisplayed(changeButton);
    }
}
