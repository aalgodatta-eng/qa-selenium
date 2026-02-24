package com.uitesting.smoke;

import com.uitesting.BaseTest;
import com.uitesting.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SmokeTests - Fast sanity checks for UITestingPlayground.com
 * Validates that each page loads and core functionality is available.
 * Run command: mvn test -Psmoke
 */
public class SmokeTests extends BaseTest {

    // ─────────────────────────────────────────────
    // HOME PAGE
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-01 | Verify home page loads successfully",
          groups = {"smoke", "homepage"})
    public void smokeHomePageLoads() {
        HomePage homePage = new HomePage(driver);
        homePage.navigateToHome();
        Assert.assertTrue(homePage.isLoaded(), "Home page did not load properly");
        logger.info("Home page loaded. Title: {}", driver.getTitle());
    }

    // ─────────────────────────────────────────────
    // DYNAMIC ID
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-02 | Dynamic ID page loads and button is visible",
          groups = {"smoke", "dynamicId"})
    public void smokeDynamicIdPageLoads() {
        DynamicIdPage page = new DynamicIdPage(driver);
        page.navigateToDynamicIdPage();
        Assert.assertTrue(page.isPageLoaded(), "Dynamic ID page did not load");
        Assert.assertTrue(page.isDynamicButtonDisplayed(), "Dynamic button not visible");
    }

    // ─────────────────────────────────────────────
    // CLASS ATTRIBUTE
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-03 | Class Attribute page loads and primary button is visible",
          groups = {"smoke", "classAttribute"})
    public void smokeClassAttributePageLoads() {
        ClassAttributePage page = new ClassAttributePage(driver);
        page.navigateToClassAttributePage();
        Assert.assertTrue(page.isPageLoaded(), "Class Attribute page did not load");
        Assert.assertTrue(page.isPrimaryButtonDisplayed(), "Primary button not visible");
    }

    // ─────────────────────────────────────────────
    // HIDDEN LAYERS
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-04 | Hidden Layers page loads and green button is visible",
          groups = {"smoke", "hiddenLayers"})
    public void smokeHiddenLayersPageLoads() {
        HiddenLayersPage page = new HiddenLayersPage(driver);
        page.navigateToHiddenLayersPage();
        Assert.assertTrue(page.isPageLoaded(), "Hidden Layers page did not load");
        Assert.assertTrue(page.isGreenButtonDisplayed(), "Green button not visible");
    }

    // ─────────────────────────────────────────────
    // LOAD DELAY
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-05 | Load Delay page fully loads within timeout",
          groups = {"smoke", "loadDelay"})
    public void smokeLoadDelayPageLoads() {
        LoadDelayPage page = new LoadDelayPage(driver);
        page.navigateToLoadDelayPage();
        Assert.assertTrue(page.isPageLoaded(), "Load Delay page button did not appear");
    }

    // ─────────────────────────────────────────────
    // AJAX DATA
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-06 | AJAX Data page loads and button is clickable",
          groups = {"smoke", "ajax"})
    public void smokeAjaxDataPageLoads() {
        AjaxDataPage page = new AjaxDataPage(driver);
        page.navigateToAjaxDataPage();
        Assert.assertTrue(page.isPageLoaded(), "AJAX Data page did not load");
    }

    // ─────────────────────────────────────────────
    // CLIENT SIDE DELAY
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-07 | Client Side Delay page loads and generate button visible",
          groups = {"smoke", "clientDelay"})
    public void smokeClientSideDelayPageLoads() {
        ClientSideDelayPage page = new ClientSideDelayPage(driver);
        page.navigateToClientSideDelayPage();
        Assert.assertTrue(page.isPageLoaded(), "Client Side Delay page did not load");
    }

    // ─────────────────────────────────────────────
    // CLICK
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-08 | Click page loads and button is visible",
          groups = {"smoke", "click"})
    public void smokeClickPageLoads() {
        ClickPage page = new ClickPage(driver);
        page.navigateToClickPage();
        Assert.assertTrue(page.isPageLoaded(), "Click page did not load");
    }

    // ─────────────────────────────────────────────
    // TEXT INPUT
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-09 | Text Input page loads and input field visible",
          groups = {"smoke", "textInput"})
    public void smokeTextInputPageLoads() {
        TextInputPage page = new TextInputPage(driver);
        page.navigateToTextInputPage();
        Assert.assertTrue(page.isPageLoaded(), "Text Input page did not load");
    }

    // ─────────────────────────────────────────────
    // SCROLLBARS
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-10 | Scrollbars page loads",
          groups = {"smoke", "scrollbars"})
    public void smokeScrollbarsPageLoads() {
        ScrollbarsPage page = new ScrollbarsPage(driver);
        page.navigateToScrollbarsPage();
        Assert.assertTrue(page.isPageLoaded(), "Scrollbars page did not load");
    }

    // ─────────────────────────────────────────────
    // DYNAMIC TABLE
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-11 | Dynamic Table page loads with data",
          groups = {"smoke", "dynamicTable"})
    public void smokeDynamicTablePageLoads() {
        DynamicTablePage page = new DynamicTablePage(driver);
        page.navigateToDynamicTablePage();
        Assert.assertTrue(page.isPageLoaded(), "Dynamic Table page did not load");
        Assert.assertTrue(page.getRowCount() > 0, "No rows found in table");
    }

    // ─────────────────────────────────────────────
    // VERIFY TEXT
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-12 | Verify Text page loads and text container visible",
          groups = {"smoke", "verifyText"})
    public void smokeVerifyTextPageLoads() {
        VerifyTextPage page = new VerifyTextPage(driver);
        page.navigateToVerifyTextPage();
        Assert.assertTrue(page.isPageLoaded(), "Verify Text page did not load");
    }

    // ─────────────────────────────────────────────
    // PROGRESS BAR
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-13 | Progress Bar page loads with start button",
          groups = {"smoke", "progressBar"})
    public void smokeProgressBarPageLoads() {
        ProgressBarPage page = new ProgressBarPage(driver);
        page.navigateToProgressBarPage();
        Assert.assertTrue(page.isPageLoaded(), "Progress Bar page did not load");
    }

    // ─────────────────────────────────────────────
    // SAMPLE APP
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-14 | Sample App page loads with login form",
          groups = {"smoke", "sampleApp"})
    public void smokeSampleAppPageLoads() {
        SampleAppPage page = new SampleAppPage(driver);
        page.navigateToSampleAppPage();
        Assert.assertTrue(page.isPageLoaded(), "Sample App page did not load");
        Assert.assertTrue(page.isLoginButtonDisplayed(), "Login button not visible");
    }

    // ─────────────────────────────────────────────
    // ALERTS
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-15 | Alerts page loads with all trigger buttons",
          groups = {"smoke", "alerts"})
    public void smokeAlertsPageLoads() {
        AlertsPage page = new AlertsPage(driver);
        page.navigateToAlertsPage();
        Assert.assertTrue(page.isPageLoaded(), "Alerts page did not load");
    }

    // ─────────────────────────────────────────────
    // SHADOW DOM
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-16 | Shadow DOM page loads and host element present",
          groups = {"smoke", "shadowDom"})
    public void smokeShadowDomPageLoads() {
        ShadowDomPage page = new ShadowDomPage(driver);
        page.navigateToShadowDomPage();
        Assert.assertTrue(page.isPageLoaded(), "Shadow DOM page did not load");
        Assert.assertTrue(page.isShadowHostPresent(), "Shadow host element not found");
    }

    // ─────────────────────────────────────────────
    // ANIMATED BUTTON
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-17 | Animated Button page loads and button is visible",
          groups = {"smoke", "animatedButton"})
    public void smokeAnimatedButtonPageLoads() {
        AnimatedButtonPage page = new AnimatedButtonPage(driver);
        page.navigateToAnimatedButtonPage();
        Assert.assertTrue(page.isPageLoaded(), "Animated Button page did not load");
        Assert.assertTrue(page.isButtonDisplayed(), "Animated button not visible");
    }

    // ─────────────────────────────────────────────
    // DISABLED INPUT
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-18 | Disabled Input page loads with enable button",
          groups = {"smoke", "disabledInput"})
    public void smokeDisabledInputPageLoads() {
        DisabledInputPage page = new DisabledInputPage(driver);
        page.navigateToDisabledInputPage();
        Assert.assertTrue(page.isPageLoaded(), "Disabled Input page did not load");
    }

    // ─────────────────────────────────────────────
    // FRAMES
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-19 | Frames page loads and contains iframes",
          groups = {"smoke", "frames"})
    public void smokeFramesPageLoads() {
        FramesPage page = new FramesPage(driver);
        page.navigateToFramesPage();
        Assert.assertTrue(page.isPageLoaded(), "Frames page did not load or has no frames");
    }

    // ─────────────────────────────────────────────
    // FILE UPLOAD
    // ─────────────────────────────────────────────
    @Test(description = "SMOKE-20 | File Upload page loads with file input visible",
          groups = {"smoke", "fileUpload"})
    public void smokeFileUploadPageLoads() {
        FileUploadPage page = new FileUploadPage(driver);
        page.navigateToFileUploadPage();
        Assert.assertTrue(page.isPageLoaded(), "File Upload page did not load");
        Assert.assertTrue(page.isFileUploadInputPresent(), "File input not present");
    }
}
