package com.uitesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * HomePage - UITestingPlayground main landing page
 */
public class HomePage extends BasePage {

    @FindBy(css = "h1.display-4")
    private WebElement pageHeader;

    @FindBy(css = "a.btn.btn-primary")
    private List<WebElement> navLinks;

    // Navigation link locators using stable text/href (NOT dynamic IDs)
    private final By dynamicIdLink = By.linkText("Dynamic ID");
    private final By classAttributeLink = By.linkText("Class Attribute");
    private final By hiddenLayersLink = By.linkText("Hidden Layers");
    private final By loadDelayLink = By.linkText("Load Delay");
    private final By ajaxDataLink = By.linkText("AJAX Data");
    private final By clientSideDelayLink = By.linkText("Client Side Delay");
    private final By clickLink = By.linkText("Click");
    private final By textInputLink = By.linkText("Text Input");
    private final By scrollbarsLink = By.linkText("Scrollbars");
    private final By dynamicTableLink = By.linkText("Dynamic Table");
    private final By verifyTextLink = By.linkText("Verify Text");
    private final By progressBarLink = By.linkText("Progress Bar");
    private final By visibilityLink = By.linkText("Visibility");
    private final By sampleAppLink = By.linkText("Sample App");
    private final By mouseOverLink = By.linkText("Mouse Over");
    private final By nonBreakingSpaceLink = By.linkText("Non-Breaking Space");
    private final By overlappedElementLink = By.linkText("Overlapped Element");
    private final By shadowDomLink = By.linkText("Shadow DOM");
    private final By alertsLink = By.linkText("Alerts");
    private final By fileUploadLink = By.linkText("File Upload");
    private final By animatedButtonLink = By.linkText("Animated Button");
    private final By disabledInputLink = By.linkText("Disabled Input");
    private final By autoWaitLink = By.linkText("Auto Wait");
    private final By framesLink = By.linkText("Frames");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToHome() {
        navigateToPage("/home");
    }

    public String getHeaderText() {
        return pageHeader.getText();
    }

    public boolean isLoaded() {
        return isDisplayed(By.cssSelector("h1.display-4"));
    }

    public int getNavigationLinksCount() {
        return navLinks.size();
    }

    public void clickDynamicId() { waitAndClick(dynamicIdLink); }
    public void clickClassAttribute() { waitAndClick(classAttributeLink); }
    public void clickHiddenLayers() { waitAndClick(hiddenLayersLink); }
    public void clickLoadDelay() { waitAndClick(loadDelayLink); }
    public void clickAjaxData() { waitAndClick(ajaxDataLink); }
    public void clickClientSideDelay() { waitAndClick(clientSideDelayLink); }
    public void clickClick() { waitAndClick(clickLink); }
    public void clickTextInput() { waitAndClick(textInputLink); }
    public void clickScrollbars() { waitAndClick(scrollbarsLink); }
    public void clickDynamicTable() { waitAndClick(dynamicTableLink); }
    public void clickVerifyText() { waitAndClick(verifyTextLink); }
    public void clickProgressBar() { waitAndClick(progressBarLink); }
    public void clickVisibility() { waitAndClick(visibilityLink); }
    public void clickSampleApp() { waitAndClick(sampleAppLink); }
    public void clickMouseOver() { waitAndClick(mouseOverLink); }
    public void clickNonBreakingSpace() { waitAndClick(nonBreakingSpaceLink); }
    public void clickOverlappedElement() { waitAndClick(overlappedElementLink); }
    public void clickShadowDom() { waitAndClick(shadowDomLink); }
    public void clickAlerts() { waitAndClick(alertsLink); }
    public void clickFileUpload() { waitAndClick(fileUploadLink); }
    public void clickAnimatedButton() { waitAndClick(animatedButtonLink); }
    public void clickDisabledInput() { waitAndClick(disabledInputLink); }
    public void clickAutoWait() { waitAndClick(autoWaitLink); }
    public void clickFrames() { waitAndClick(framesLink); }
}
