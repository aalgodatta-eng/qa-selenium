package com.uitesting.utils;

import com.uitesting.config.ConfigManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * WaitUtils - Centralized wait utilities for robust test execution
 */
public class WaitUtils {

    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    private static final int EXPLICIT_WAIT = ConfigManager.getInstance().getExplicitWait();

    private WaitUtils() {}

    public static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT));
    }

    public static WebDriverWait getWait(WebDriver driver, int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return getWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisible(WebDriver driver, By locator, int timeoutSec) {
        return getWait(driver, timeoutSec).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return getWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator, int timeoutSec) {
        return getWait(driver, timeoutSec).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static void waitForInvisibility(WebDriver driver, By locator) {
        getWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForTextInElement(WebDriver driver, By locator, String text) {
        return getWait(driver).until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static void waitForPageLoad(WebDriver driver) {
        logger.debug("Waiting for page to fully load");
        getWait(driver).until((ExpectedCondition<Boolean>) d -> {
            assert d != null;
            return ((JavascriptExecutor) d)
                    .executeScript("return document.readyState").equals("complete");
        });
    }

    public static void fluentWaitForVisible(WebDriver driver, By locator, int timeoutSec, int pollMillis) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSec))
                .pollingEvery(Duration.ofMillis(pollMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void hardSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean isElementPresent(WebDriver driver, By locator) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            return !driver.findElements(locator).isEmpty();
        } catch (Exception e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigManager.getInstance().getImplicitWait()));
        }
    }

    public static boolean isElementVisible(WebDriver driver, By locator) {
        try {
            WebElement el = driver.findElement(locator);
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
