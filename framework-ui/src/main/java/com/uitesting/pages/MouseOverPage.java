package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * MouseOverPage - Mouse hover changes the DOM; element may disappear on move away.
 * Must keep mouse on element while interacting.
 */
public class MouseOverPage extends BasePage {

    // ...existing code...
    private final By clickLinkBtn = By.xpath("//a[text()='Click me']");
    private final By clickCountLabel = By.id("clickCount");

    public MouseOverPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToMouseOverPage() {
        navigateToPage("/mouseover");
    }

    /**
     * Hover and click the link using Actions - maintains mouse position
     */
    public void hoverAndClickLink() {
        logger.info("Hovering and clicking link via Actions");
        WebElement link = WaitUtils.waitForVisible(driver, clickLinkBtn);
        new Actions(driver)
            .moveToElement(link)
            .click()
            .perform();
    }

    /**
     * Click link N times using hover-click sequence
     */
    public void clickLinkNTimes(int n) {
        for (int i = 0; i < n; i++) {
            hoverAndClickLink();
            WaitUtils.hardSleep(200);
        }
    }

    public int getClickCount() {
        try {
            String countText = WaitUtils.waitForVisible(driver, clickCountLabel, 5).getText();
            return Integer.parseInt(countText.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isPageLoaded() {
        return isDisplayed(clickLinkBtn);
    }
}
