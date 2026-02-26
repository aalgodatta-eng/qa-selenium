package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
// ...existing code...
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * AnimatedButtonPage - Wait for animation to complete before clicking.
 * Clicking an animating button may miss or cause unexpected behavior.
 */
public class AnimatedButtonPage extends BasePage {

    // The button has a CSS animation class initially
    private final By animatedButton = By.id("animationButton");
    // ...existing code...

    public AnimatedButtonPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToAnimatedButtonPage() {
        navigateToPage("/animation");
    }

    /**
     * Wait for animation CSS class to be removed, then click
     */
    public void waitForAnimationStopAndClick() {
        logger.info("Waiting for button animation to stop");
        // Wait until the animating class is removed from the button
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(driver -> {
            WebElement btn = driver.findElement(animatedButton);
            String classes = btn.getAttribute("class");
            // Animation stops when 'animationButton' class is removed
            return classes != null && !classes.contains("animationButton");
        });
        logger.info("Animation stopped, clicking button");
        WaitUtils.waitForClickable(driver, animatedButton).click();
    }

    /**
     * Click button while animating (NEGATIVE test - demonstrates the problem)
     */
    public void clickButtonDuringAnimation() {
        logger.info("Attempting click during animation (negative test)");
        WaitUtils.waitForClickable(driver, animatedButton, 5).click();
    }

    public boolean isButtonAnimating() {
        try {
            WebElement btn = driver.findElement(animatedButton);
            String classes = btn.getAttribute("class");
            return classes != null && classes.contains("animationButton");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isButtonDisplayed() {
        return isDisplayed(animatedButton);
    }

    public String getButtonClass() {
        return driver.findElement(animatedButton).getAttribute("class");
    }

    public boolean isPageLoaded() {
        return isDisplayed(animatedButton);
    }
}
