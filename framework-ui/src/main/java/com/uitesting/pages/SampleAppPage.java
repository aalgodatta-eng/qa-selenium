package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * SampleAppPage - Demo app with dynamically generated element attributes.
 * Login form with dynamic element IDs/names - use stable relative locators.
 */
public class SampleAppPage extends BasePage {

    // Use name attribute (stable) instead of dynamic IDs
    private final By usernameField = By.name("UserName");
    private final By passwordField = By.name("Password");
    private final By loginButton = By.cssSelector("#login");
    private final By loggedUserLabel = By.id("logInStatus");
    // ...existing code...

    public SampleAppPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToSampleAppPage() {
        navigateToPage("/sampleapp");
    }

    public void enterUsername(String username) {
        logger.info("Entering username: {}", username);
        waitAndType(usernameField, username);
    }

    public void enterPassword(String password) {
        logger.info("Entering password");
        waitAndType(passwordField, password);
    }

    public void clickLoginButton() {
        WaitUtils.waitForClickable(driver, loginButton).click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public String getLoginStatus() {
        try {
            return WaitUtils.waitForVisible(driver, loggedUserLabel, 5).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isLoggedIn() {
        String status = getLoginStatus();
        return status.contains("Welcome") || status.contains("logged in");
    }

    public boolean isLoginFailed() {
        String status = getLoginStatus();
        return status.contains("Invalid") || status.contains("invalid") || status.contains("wrong");
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }

    public boolean isPageLoaded() {
        return isDisplayed(usernameField) && isDisplayed(passwordField);
    }
}
