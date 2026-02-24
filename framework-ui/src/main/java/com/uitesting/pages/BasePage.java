package com.uitesting.pages;

import com.uitesting.config.ConfigManager;
import com.uitesting.utils.JSUtils;
import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasePage - Parent class for all Page Objects
 */
public abstract class BasePage {

    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected WebDriver driver;
    protected ConfigManager config;
    protected String baseUrl;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigManager.getInstance();
        this.baseUrl = config.getBaseUrl();
        PageFactory.initElements(driver, this);
    }

    public void navigateToPage(String path) {
        String url = baseUrl + path;
        logger.info("Navigating to: {}", url);
        driver.get(url);
        WaitUtils.waitForPageLoad(driver);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    protected WebElement waitAndClick(By locator) {
        WebElement element = WaitUtils.waitForClickable(driver, locator);
        element.click();
        return element;
    }

    protected WebElement waitAndType(By locator, String text) {
        WebElement element = WaitUtils.waitForVisible(driver, locator);
        element.clear();
        element.sendKeys(text);
        return element;
    }

    protected String getText(By locator) {
        return WaitUtils.waitForVisible(driver, locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        return WaitUtils.isElementVisible(driver, locator);
    }

    protected void scrollToElement(WebElement element) {
        JSUtils.scrollToElement(driver, element);
    }

    protected void clickViaJS(WebElement element) {
        JSUtils.clickViaJS(driver, element);
    }

    protected void selectByVisibleText(By locator, String text) {
        new Select(driver.findElement(locator)).selectByVisibleText(text);
    }
}
