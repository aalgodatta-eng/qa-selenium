package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
// ...existing code...
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * ProgressBarPage - Follow progress bar and stop process when target reached.
 */
public class ProgressBarPage extends BasePage {

    private final By startButton = By.id("startButton");
    private final By stopButton = By.id("stopButton");
    private final By progressBar = By.cssSelector("#progressBar");
    // ...existing code...
    private final By resultLabel = By.id("result");

    public ProgressBarPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToProgressBarPage() {
        navigateToPage("/progressbar");
    }

    public void clickStart() {
        logger.info("Starting progress bar");
        WaitUtils.waitForClickable(driver, startButton).click();
    }

    public void clickStop() {
        logger.info("Stopping progress bar");
        WaitUtils.waitForClickable(driver, stopButton).click();
    }

    /**
     * Get current progress value (0-100)
     */
    public int getCurrentProgress() {
        try {
            WebElement bar = driver.findElement(progressBar);
            String ariaValue = bar.getAttribute("aria-valuenow");
            if (ariaValue != null) {
                return Integer.parseInt(ariaValue);
            }
            String text = bar.getText().replace("%", "").trim();
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Wait for progress to reach target percentage, then stop
     * Uses polling to check progress level
     */
    public void waitForProgressAndStop(int targetPercent) {
        logger.info("Waiting for progress to reach {}%", targetPercent);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        wait.until(d -> {
            int current = getCurrentProgress();
            logger.debug("Current progress: {}%", current);
            return current >= targetPercent;
        });
        clickStop();
    }

    /**
     * Verify progress stops near the target value (within tolerance)
     */
    public boolean isProgressNearTarget(int target, int tolerance) {
        int current = getCurrentProgress();
        logger.info("Final progress: {}%, target: {}%, tolerance: {}%", current, target, tolerance);
        return Math.abs(current - target) <= tolerance;
    }

    public String getResultText() {
        try {
            return WaitUtils.waitForVisible(driver, resultLabel, 5).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isProgressBarDisplayed() {
        return isDisplayed(progressBar);
    }

    public boolean isPageLoaded() {
        return isDisplayed(startButton) && isDisplayed(progressBar);
    }

    /**
     * Reset progress bar (click start then stop immediately)
     */
    public void reset() {
        clickStart();
        WaitUtils.hardSleep(100);
        clickStop();
    }
}
