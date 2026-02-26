package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * FramesPage - Find and interact with elements inside nested frames.
 */
public class FramesPage extends BasePage {

    // Outer frame
    private final By outerFrame = By.id("frame");
    // Inner frame inside the outer
    private final By innerFrame = By.id("frame");
    private final By frameButton = By.id("button");

    public FramesPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToFramesPage() {
        navigateToPage("/frames");
    }

    /**
     * Switch to the first (outer) frame
     */
    public void switchToOuterFrame() {
        logger.info("Switching to outer frame");
        WaitUtils.waitForPresence(driver, By.tagName("iframe"));
        WebElement frame = driver.findElements(By.tagName("iframe")).getFirst();
        driver.switchTo().frame(frame);
    }

    /**
     * Switch to inner nested frame
     */
    public void switchToInnerFrame() {
        logger.info("Switching to inner frame");
        try {
            WebElement innerIframe = driver.findElement(By.tagName("iframe"));
            driver.switchTo().frame(innerIframe);
        } catch (Exception e) {
            logger.warn("Inner frame not found: {}", e.getMessage());
        }
    }

    /**
     * Switch back to the default content (main page)
     */
    public void switchToDefaultContent() {
        logger.info("Switching back to default content");
        driver.switchTo().defaultContent();
    }

    /**
     * Switch to parent frame
     */
    public void switchToParentFrame() {
        logger.info("Switching to parent frame");
        driver.switchTo().parentFrame();
    }

    /**
     * Get frame button text from inside frame
     */
    public String getFrameButtonText() {
        try {
            return WaitUtils.waitForVisible(driver, frameButton, 5).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Click button inside a frame
     */
    public void clickFrameButton() {
        logger.info("Clicking button inside frame");
        WaitUtils.waitForClickable(driver, frameButton, 5).click();
    }

    /**
     * Complete nested frame interaction flow
     */
    public String readTextFromNestedFrame() {
        try {
            // Switch to outer frame
            WaitUtils.waitForPresence(driver, By.tagName("iframe"));
            java.util.List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            if (!iframes.isEmpty()) {
                driver.switchTo().frame(iframes.getFirst());
                // Try to find nested iframe
                java.util.List<WebElement> innerIframes = driver.findElements(By.tagName("iframe"));
                if (!innerIframes.isEmpty()) {
                    driver.switchTo().frame(innerIframes.getFirst());
                }
                // Get text from button or any text element
                try {
                    return driver.findElement(By.tagName("button")).getText();
                } catch (Exception e) {
                    return driver.findElement(By.tagName("body")).getText();
                }
            }
        } catch (Exception e) {
            logger.error("Error reading from nested frame: {}", e.getMessage());
        } finally {
            driver.switchTo().defaultContent();
        }
        return "";
    }

    public int getFrameCount() {
        return driver.findElements(By.tagName("iframe")).size();
    }

    public boolean isPageLoaded() {
        return !driver.findElements(By.tagName("iframe")).isEmpty();
    }
}
