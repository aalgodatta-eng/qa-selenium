package com.uitesting.pages;

import com.uitesting.utils.JSUtils;
import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
// ...existing code...
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * OverlappedElementPage - An input field is covered by a fixed sticky header.
 * Must scroll it into view properly or use JS to interact.
 */
public class OverlappedElementPage extends BasePage {

    private final By nameField = By.id("name");
    private final By idField = By.id("id");

    public OverlappedElementPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToOverlappedElementPage() {
        navigateToPage("/overlapped");
    }

    /**
     * Scroll field into view using JS (element may be behind sticky header)
     * Then click and type
     */
    public void enterName(String name) {
        logger.info("Entering name into overlapped field: {}", name);
        WebElement field = WaitUtils.waitForPresence(driver, nameField);
        // Scroll into view accounting for sticky header
        JSUtils.executeScript(driver,
            "arguments[0].scrollIntoView({block: 'center'});", field);
        WaitUtils.hardSleep(300);
        field.click();
        field.clear();
        field.sendKeys(name);
    }

    /**
     * Enter text using Actions scroll + sendKeys (handles overlapping header)
     */
    public void enterNameViaActions(String name) {
        logger.info("Entering name via Actions class");
        WebElement field = WaitUtils.waitForPresence(driver, nameField);
        new Actions(driver)
            .scrollToElement(field)
            .click(field)
            .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
            .sendKeys(name)
            .perform();
    }

    public void enterIdValue(String id) {
        logger.info("Entering ID value: {}", id);
        WebElement field = WaitUtils.waitForPresence(driver, idField);
        JSUtils.executeScript(driver, "arguments[0].scrollIntoView({block: 'center'});", field);
        WaitUtils.hardSleep(300);
        field.click();
        field.clear();
        field.sendKeys(id);
    }

    public String getNameFieldValue() {
        return driver.findElement(nameField).getAttribute("value");
    }

    public String getIdFieldValue() {
        return driver.findElement(idField).getAttribute("value");
    }

    public boolean isNameFieldVisible() {
        try {
            WebElement field = driver.findElement(nameField);
            return field.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPageLoaded() {
        return isDisplayed(nameField);
    }
}
