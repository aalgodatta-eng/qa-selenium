package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * AlertsPage - Handle JavaScript alerts, confirmations, and prompts.
 */
public class AlertsPage extends BasePage {

    private final By alertButton = By.id("alertButton");
    private final By confirmButton = By.id("confirmButton");
    private final By promptButton = By.id("promptButton");
    private final By confirmResult = By.id("confirmResult");
    private final By promptResult = By.id("promptResult");

    public AlertsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToAlertsPage() {
        navigateToPage("/alerts");
    }

    /**
     * Wait for alert to appear and return Alert object
     */
    private Alert waitForAlert() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Click Alert button and accept the alert
     */
    public void triggerAndAcceptAlert() {
        logger.info("Triggering and accepting JavaScript alert");
        WaitUtils.waitForClickable(driver, alertButton).click();
        Alert alert = waitForAlert();
        String alertText = alert.getText();
        logger.info("Alert text: {}", alertText);
        alert.accept();
    }

    /**
     * Get alert text without dismissing
     */
    public String getAlertText() {
        Alert alert = waitForAlert();
        return alert.getText();
    }

    /**
     * Trigger confirm dialog and accept
     */
    public void triggerAndAcceptConfirm() {
        logger.info("Triggering and accepting confirm dialog");
        WaitUtils.waitForClickable(driver, confirmButton).click();
        Alert alert = waitForAlert();
        logger.info("Confirm text: {}", alert.getText());
        alert.accept();
    }

    /**
     * Trigger confirm dialog and dismiss (Cancel)
     */
    public void triggerAndDismissConfirm() {
        logger.info("Triggering and dismissing confirm dialog");
        WaitUtils.waitForClickable(driver, confirmButton).click();
        Alert alert = waitForAlert();
        alert.dismiss();
    }

    /**
     * Trigger prompt dialog, enter text, and accept
     */
    public void triggerPromptAndEnterText(String text) {
        logger.info("Triggering prompt and entering: {}", text);
        WaitUtils.waitForClickable(driver, promptButton).click();
        Alert alert = waitForAlert();
        alert.sendKeys(text);
        alert.accept();
    }

    /**
     * Trigger prompt dialog and cancel
     */
    public void triggerAndDismissPrompt() {
        logger.info("Triggering and dismissing prompt");
        WaitUtils.waitForClickable(driver, promptButton).click();
        Alert alert = waitForAlert();
        alert.dismiss();
    }

    public String getConfirmResult() {
        return getText(confirmResult);
    }

    public String getPromptResult() {
        return getText(promptResult);
    }

    public boolean isPageLoaded() {
        return isDisplayed(alertButton);
    }
}
