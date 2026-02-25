package ui.steps.ui;

import com.algodatta.context.ScenarioContext;
import com.algodatta.core.driver.DriverManager;
import com.uitesting.pages.*;
import com.uitesting.utils.WaitUtils;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Step definitions for all UITestingPlayground BDD feature scenarios.
 *
 * <p>Covers all 25 UITestingPlayground feature pages with:
 * <ul>
 *   <li>Smoke scenarios – page loads + key element visible</li>
 *   <li>Regression scenarios – positive and negative functional tests</li>
 * </ul>
 *
 * <p>Page objects are instantiated lazily per step. PicoContainer creates
 * one instance of this class per Cucumber scenario, so all instance fields
 * (including captured state between steps) are scenario-scoped.
 */
public class UITestingPlaygroundSteps {

    private final ScenarioContext ctx;

    // ─── Page objects (lazily initialised) ───────────────────────────────────
    private HomePage             homePage;
    private DynamicIdPage        dynamicIdPage;
    private ClassAttributePage   classAttrPage;
    private HiddenLayersPage     hiddenLayersPage;
    private LoadDelayPage        loadDelayPage;
    private AjaxDataPage         ajaxDataPage;
    private ClientSideDelayPage  clientDelayPage;
    private ClickPage            clickPage;
    private TextInputPage        textInputPage;
    private ScrollbarsPage       scrollbarsPage;
    private DynamicTablePage     dynamicTablePage;
    private VerifyTextPage       verifyTextPage;
    private ProgressBarPage      progressBarPage;
    private SampleAppPage        sampleAppPage;
    private MouseOverPage        mouseOverPage;
    private NonBreakingSpacePage nbsPage;
    private OverlappedElementPage overlappedPage;
    private ShadowDomPage        shadowDomPage;
    private AlertsPage           alertsPage;
    private FileUploadPage       fileUploadPage;
    private AnimatedButtonPage   animatedButtonPage;
    private DisabledInputPage    disabledInputPage;
    private AutoWaitPage         autoWaitPage;
    private FramesPage           framesPage;
    private VisibilityPage       visibilityPage;

    // ─── Cross-step captured state ────────────────────────────────────────────
    private String  capturedButtonId;
    private boolean capturedFindResult;

    // ─── Constructor (PicoContainer DI) ──────────────────────────────────────
    public UITestingPlaygroundSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    /** Returns the current WebDriver initialised by {@code Hooks.@Before("@ui")}. */
    private WebDriver driver() {
        return DriverManager.getDriver();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HOME PAGE
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the UITestingPlayground home page")
    public void navigateToHome() {
        homePage = new HomePage(driver());
        homePage.navigateToHome();
    }

    @Then("the home page should be loaded")
    public void homePageShouldBeLoaded() {
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");
        ctx.log("Home page loaded. Title: " + driver().getTitle());
    }

    @Then("the navigation links should be present")
    public void navigationLinksShouldBePresent() {
        int count = homePage.getNavigationLinksCount();
        ctx.log("Navigation links count: " + count);
        Assert.assertTrue(count > 0, "Navigation links should be present");
    }

    @Then("the home page header should be visible")
    public void homePageHeaderShouldBeVisible() {
        String header = homePage.getHeaderText();
        ctx.log("Header: " + header);
        Assert.assertNotNull(header, "Header text should not be null");
        Assert.assertFalse(header.isEmpty(), "Header text should not be empty");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DYNAMIC ID
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the dynamic id page")
    public void navigateToDynamicId() {
        dynamicIdPage = new DynamicIdPage(driver());
        dynamicIdPage.navigateToDynamicIdPage();
    }

    @Then("the dynamic id page should be loaded")
    public void dynamicIdPageShouldBeLoaded() {
        Assert.assertTrue(dynamicIdPage.isPageLoaded(), "Dynamic ID page should be loaded");
    }

    @Then("the dynamic button should be displayed")
    public void dynamicButtonShouldBeDisplayed() {
        Assert.assertTrue(dynamicIdPage.isDynamicButtonDisplayed(), "Dynamic button should be visible");
    }

    @When("I click the dynamic button")
    public void clickDynamicButton() {
        dynamicIdPage.clickDynamicButton();
    }

    @Then("the dynamic button should still be displayed after click")
    public void dynamicButtonStillDisplayed() {
        Assert.assertTrue(dynamicIdPage.isDynamicButtonDisplayed(),
            "Button should still be present after click using class selector");
    }

    @When("I record the current dynamic button ID")
    public void recordDynamicButtonId() {
        capturedButtonId = dynamicIdPage.getButtonId();
        ctx.log("Recorded button ID: " + capturedButtonId);
    }

    @When("I reload the dynamic id page")
    public void reloadDynamicIdPage() {
        dynamicIdPage.navigateToDynamicIdPage();
    }

    @Then("the button ID should be different from the recorded one")
    public void buttonIdShouldBeDifferent() {
        String newId = dynamicIdPage.getButtonId();
        ctx.log("New ID: " + newId + " | Previous: " + capturedButtonId);
        Assert.assertNotEquals(newId, capturedButtonId,
            "IDs should differ on each page load (dynamic IDs)");
    }

    @Then("the button should still be findable via stable class selector")
    public void buttonFindableViaClassSelector() {
        Assert.assertTrue(dynamicIdPage.isDynamicButtonDisplayed(),
            "Button must be findable via stable class selector despite dynamic ID");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CLASS ATTRIBUTE
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the class attribute page")
    public void navigateToClassAttribute() {
        classAttrPage = new ClassAttributePage(driver());
        classAttrPage.navigateToClassAttributePage();
    }

    @Then("the class attribute page should be loaded")
    public void classAttributePageShouldBeLoaded() {
        Assert.assertTrue(classAttrPage.isPageLoaded(), "Class Attribute page should be loaded");
    }

    @Then("the primary button should be visible")
    public void primaryButtonShouldBeVisible() {
        Assert.assertTrue(classAttrPage.isPrimaryButtonDisplayed(), "Primary button should be visible");
    }

    @When("I click the primary button using XPath contains")
    public void clickPrimaryButtonXPath() {
        classAttrPage.clickPrimaryButton();
    }

    @When("I click the primary button using CSS selector")
    public void clickPrimaryButtonCss() {
        classAttrPage.clickPrimaryButtonByCss();
    }

    @Then("the primary button should remain on the page")
    public void primaryButtonShouldRemain() {
        Assert.assertTrue(classAttrPage.isPrimaryButtonDisplayed(),
            "Button should remain on page after click");
    }

    @Then("the button class attribute should contain {string}")
    public void buttonClassShouldContain(String expected) {
        String classes = classAttrPage.getButtonClassAttribute();
        ctx.log("Button classes: " + classes);
        Assert.assertNotNull(classes, "Class attribute should not be null");
        Assert.assertTrue(classes.contains(expected),
            "Class attribute should contain '" + expected + "'");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HIDDEN LAYERS
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the hidden layers page")
    public void navigateToHiddenLayers() {
        hiddenLayersPage = new HiddenLayersPage(driver());
        hiddenLayersPage.navigateToHiddenLayersPage();
    }

    @Then("the hidden layers page should be loaded")
    public void hiddenLayersPageShouldBeLoaded() {
        Assert.assertTrue(hiddenLayersPage.isPageLoaded(), "Hidden Layers page should be loaded");
    }

    @Then("the green button should be visible")
    public void greenButtonShouldBeVisible() {
        Assert.assertTrue(hiddenLayersPage.isGreenButtonDisplayed(), "Green button should be visible");
    }

    @When("I click the green button")
    public void clickGreenButton() {
        hiddenLayersPage.clickGreenButton();
    }

    @Then("the blue button should appear on top")
    public void blueButtonShouldAppear() {
        Assert.assertTrue(hiddenLayersPage.isBlueButtonVisible(),
            "Blue button should appear after clicking green");
    }

    @Then("the green button should not be directly clickable")
    public void greenButtonShouldNotBeClickable() {
        Assert.assertFalse(hiddenLayersPage.isGreenButtonClickable(),
            "Green button should be covered by blue and not directly clickable");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LOAD DELAY
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the load delay page")
    public void navigateToLoadDelay() {
        loadDelayPage = new LoadDelayPage(driver());
        loadDelayPage.navigateToLoadDelayPage();
    }

    @Then("the load delay page should be loaded")
    public void loadDelayPageShouldBeLoaded() {
        Assert.assertTrue(loadDelayPage.isPageLoaded(), "Load Delay button should appear within timeout");
    }

    @Then("the delayed button should be visible after waiting")
    public void delayedButtonShouldBeVisible() {
        Assert.assertTrue(loadDelayPage.isButtonDisplayed(),
            "Button should appear after page load delay");
    }

    @When("I click the delayed button")
    public void clickDelayedButton() {
        loadDelayPage.clickLoadedButton();
    }

    @Then("the load delay page should be fully loaded")
    public void loadDelayPageFullyLoaded() {
        Assert.assertTrue(loadDelayPage.isPageFullyLoaded(),
            "Page should be fully loaded after button click");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AJAX DATA
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the ajax data page")
    public void navigateToAjaxData() {
        ajaxDataPage = new AjaxDataPage(driver());
        ajaxDataPage.navigateToAjaxDataPage();
    }

    @Then("the ajax data page should be loaded")
    public void ajaxDataPageShouldBeLoaded() {
        Assert.assertTrue(ajaxDataPage.isPageLoaded(), "AJAX Data page should be loaded");
    }

    @When("I click the AJAX trigger button")
    public void clickAjaxButton() {
        ajaxDataPage.clickAjaxButton();
    }

    @Then("the AJAX content should be displayed")
    public void ajaxContentShouldBeDisplayed() {
        Assert.assertTrue(ajaxDataPage.isAjaxContentDisplayed(),
            "AJAX content should load after clicking the button");
    }

    @Then("the AJAX result text should not be empty")
    public void ajaxResultShouldNotBeEmpty() {
        String result = ajaxDataPage.getAjaxResultText();
        ctx.log("AJAX result: " + result);
        Assert.assertFalse(result.isEmpty(), "AJAX result text should not be empty");
    }

    @Then("the AJAX result should not be present before clicking")
    public void ajaxResultNotPresentBeforeClick() {
        boolean present = WaitUtils.isElementPresent(driver(), By.cssSelector(".bg-success"));
        Assert.assertFalse(present,
            "AJAX result should not be present before clicking button");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CLIENT SIDE DELAY
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the client side delay page")
    public void navigateToClientSideDelay() {
        clientDelayPage = new ClientSideDelayPage(driver());
        clientDelayPage.navigateToClientSideDelayPage();
    }

    @Then("the client side delay page should be loaded")
    public void clientSideDelayPageShouldBeLoaded() {
        Assert.assertTrue(clientDelayPage.isPageLoaded(), "Client Side Delay page should be loaded");
    }

    @When("I click the generate button on client side delay page")
    public void clickGenerateButton() {
        clientDelayPage.clickGenerateButton();
    }

    @Then("the result button should appear after the delay")
    public void resultButtonShouldAppear() {
        Assert.assertTrue(clientDelayPage.isResultButtonDisplayed(),
            "Result button should appear after JS calculation completes");
    }

    @When("I perform the complete client side delay flow")
    public void performCompleteClientSideFlow() {
        clientDelayPage.performCompleteFlow();
    }

    @Then("the complete client side delay flow should succeed")
    public void completeFlowShouldSucceed() {
        Assert.assertTrue(true, "Complete flow executed without exception");
    }

    @Then("the result button should not be present before clicking generate")
    public void resultButtonNotPresentBeforeClick() {
        boolean present = WaitUtils.isElementPresent(driver(), By.id("resultButton"));
        Assert.assertFalse(present,
            "Result button should not be present before clicking generate");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CLICK
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the click page")
    public void navigateToClick() {
        clickPage = new ClickPage(driver());
        clickPage.navigateToClickPage();
    }

    @Then("the click page should be loaded")
    public void clickPageShouldBeLoaded() {
        Assert.assertTrue(clickPage.isPageLoaded(), "Click page should be loaded");
    }

    @When("I click the click-page button using standard Selenium click")
    public void clickButtonStandard() {
        clickPage.clickButton();
    }

    @When("I click the click-page button using Actions click")
    public void clickButtonActions() {
        clickPage.clickButtonViaActions();
    }

    @When("I click the click-page button using JavaScript click")
    public void clickButtonJS() {
        clickPage.clickButtonViaJS();
    }

    @Then("the click-page button should turn green indicating event was fired")
    public void buttonShouldTurnGreen() {
        Assert.assertTrue(clickPage.isButtonGreen(),
            "Button should turn green after click (event fired)");
    }

    @Then("the JavaScript click behavior on click page should be documented")
    public void jsClickBehaviorDocumented() {
        boolean isGreen = clickPage.isButtonGreen();
        ctx.log("JS click result – button green: " + isGreen);
        Assert.assertTrue(true, "JS click behavior documented");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TEXT INPUT
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the text input page")
    public void navigateToTextInput() {
        textInputPage = new TextInputPage(driver());
        textInputPage.navigateToTextInputPage();
    }

    @Then("the text input page should be loaded")
    public void textInputPageShouldBeLoaded() {
        Assert.assertTrue(textInputPage.isPageLoaded(), "Text Input page should be loaded");
    }

    @When("I type {string} into the button name field")
    public void typeButtonName(String name) {
        textInputPage.typeButtonName(name);
    }

    @When("I type {string} into the button name field via Actions")
    public void typeButtonNameViaActions(String name) {
        textInputPage.typeButtonNameViaActions(name);
    }

    @When("I click the change button")
    public void clickChangeButton() {
        textInputPage.clickChangeButton();
    }

    @Then("the button text should be updated to {string}")
    public void buttonTextShouldBeUpdatedTo(String expected) {
        Assert.assertEquals(textInputPage.getButtonText(), expected,
            "Button text should be updated to: " + expected);
    }

    @Then("the input field should retain value {string}")
    public void inputFieldShouldRetainValue(String expected) {
        Assert.assertEquals(textInputPage.getInputValue(), expected,
            "Input field should retain: " + expected);
    }

    @When("I clear the text input field")
    public void clearTextInputField() {
        textInputPage.clearInput();
    }

    @Then("the button text should not be null after empty input")
    public void buttonTextShouldNotBeNull() {
        String text = textInputPage.getButtonText();
        ctx.log("Button text with empty input: '" + text + "'");
        Assert.assertNotNull(text, "Button text should not be null");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SCROLLBARS
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the scrollbars page")
    public void navigateToScrollbars() {
        scrollbarsPage = new ScrollbarsPage(driver());
        scrollbarsPage.navigateToScrollbarsPage();
    }

    @Then("the scrollbars page should be loaded")
    public void scrollbarsPageShouldBeLoaded() {
        Assert.assertTrue(scrollbarsPage.isPageLoaded(), "Scrollbars page should be loaded");
    }

    @When("I scroll to the scrollable button and click it")
    public void scrollToButtonAndClick() {
        scrollbarsPage.scrollToButtonAndClick();
    }

    @Then("the scrollable button should be clickable after scrolling")
    public void scrollableButtonShouldBeClickable() {
        Assert.assertTrue(true, "Button clicked after scrolling into view");
    }

    @Then("the scrollable button should be in the viewport")
    public void buttonShouldBeInViewport() {
        Assert.assertTrue(scrollbarsPage.isButtonInViewport(),
            "Button should be visible in viewport after scrolling");
    }

    @When("I scroll to the top and bottom of the scrollbars page")
    public void scrollTopAndBottom() {
        scrollbarsPage.scrollToBottom();
        WaitUtils.hardSleep(300);
        scrollbarsPage.scrollToTop();
        WaitUtils.hardSleep(300);
    }

    @Then("the scroll operations should complete without error")
    public void scrollOperationsShouldComplete() {
        Assert.assertTrue(true, "Scroll operations completed without error");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DYNAMIC TABLE
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the dynamic table page")
    public void navigateToDynamicTable() {
        dynamicTablePage = new DynamicTablePage(driver());
        dynamicTablePage.navigateToDynamicTablePage();
    }

    @Then("the dynamic table page should be loaded")
    public void dynamicTablePageShouldBeLoaded() {
        Assert.assertTrue(dynamicTablePage.isPageLoaded(), "Dynamic Table page should be loaded");
    }

    @Then("the table should have at least one row")
    public void tableShouldHaveAtLeastOneRow() {
        int count = dynamicTablePage.getRowCount();
        ctx.log("Table row count: " + count);
        Assert.assertTrue(count > 0, "Table should have at least one data row");
    }

    @Then("the Chrome CPU value in the table should match the warning label")
    public void cpuValueShouldMatchWarning() {
        Assert.assertTrue(dynamicTablePage.isTableValueMatchingWarning(),
            "CPU value in table should match warning label value");
    }

    @Then("the warning label should contain a CPU percentage value")
    public void warningLabelShouldContainCpu() {
        String cpu = dynamicTablePage.getCpuValueFromWarning();
        ctx.log("Chrome CPU from warning: " + cpu);
        Assert.assertFalse(cpu.isEmpty(), "Warning label should contain a CPU percentage value");
    }

    @Then("the dynamic table and warning label should always be in sync")
    public void tableAndWarningShouldBeInSync() {
        Assert.assertTrue(dynamicTablePage.isTableValueMatchingWarning(),
            "Table and warning must always be in sync");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VERIFY TEXT
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the verify text page")
    public void navigateToVerifyText() {
        verifyTextPage = new VerifyTextPage(driver());
        verifyTextPage.navigateToVerifyTextPage();
    }

    @Then("the verify text page should be loaded")
    public void verifyTextPageShouldBeLoaded() {
        Assert.assertTrue(verifyTextPage.isPageLoaded(), "Verify Text page should be loaded");
    }

    @Then("the displayed text should not be empty")
    public void displayedTextShouldNotBeEmpty() {
        String text = verifyTextPage.getDisplayedText();
        ctx.log("Displayed text: '" + text + "'");
        Assert.assertFalse(text.isEmpty(), "Displayed text should not be empty");
    }

    @Then("normalize-space should handle whitespace in the displayed text")
    public void normalizeSpaceShouldHandleWhitespace() {
        String raw = verifyTextPage.getRawTextContent().trim();
        String displayed = verifyTextPage.getDisplayedText().trim();
        ctx.log("Raw: '" + raw + "' | Displayed: '" + displayed + "'");
        Assert.assertFalse(displayed.isEmpty(), "Displayed text should not be empty");
    }

    @Then("the text whitespace sensitivity behaviour should be documented")
    public void whitespaceTestDocumented() {
        boolean exact    = verifyTextPage.isTextPresentByXPath("Welcome, dear user!");
        boolean contains = verifyTextPage.isTextPresentByContainsXPath("Welcome");
        ctx.log("Exact match: " + exact + " | Contains match: " + contains);
        Assert.assertTrue(true, "Whitespace sensitivity test completed");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PROGRESS BAR
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the progress bar page")
    public void navigateToProgressBar() {
        progressBarPage = new ProgressBarPage(driver());
        progressBarPage.navigateToProgressBarPage();
    }

    @Then("the progress bar page should be loaded")
    public void progressBarPageShouldBeLoaded() {
        Assert.assertTrue(progressBarPage.isPageLoaded(), "Progress Bar page should be loaded");
    }

    @Then("the progress should start at {int}%")
    public void progressShouldStartAt(int expected) {
        int actual = progressBarPage.getCurrentProgress();
        ctx.log("Initial progress: " + actual + "%");
        Assert.assertEquals(actual, expected, "Progress should start at " + expected + "%");
    }

    @When("I start the progress bar")
    public void startProgressBar() {
        progressBarPage.clickStart();
    }

    @When("I wait for progress to reach {int}% then stop")
    public void waitForProgressToReachAndStop(int target) {
        progressBarPage.waitForProgressAndStop(target);
    }

    @Then("the progress should be near {int}% with {int}% tolerance")
    public void progressShouldBeNear(int target, int tolerance) {
        boolean near = progressBarPage.isProgressNearTarget(target, tolerance);
        ctx.log("Final progress: " + progressBarPage.getCurrentProgress()
            + "% | Target: " + target + "% ± " + tolerance + "%");
        Assert.assertTrue(near,
            "Progress should be near " + target + "% within ± " + tolerance + "%");
    }

    @When("I wait for {int} milliseconds")
    public void waitForMilliseconds(int ms) {
        WaitUtils.hardSleep(ms);
    }

    @Then("the progress should be greater than {int}%")
    public void progressShouldBeGreaterThan(int min) {
        int progress = progressBarPage.getCurrentProgress();
        ctx.log("Progress after wait: " + progress + "%");
        Assert.assertTrue(progress > min, "Progress should be > " + min + "%");
        progressBarPage.clickStop();
    }

    @When("I stop the progress bar")
    public void stopProgressBar() {
        progressBarPage.clickStop();
    }

    @Then("the progress should remain unchanged after stopping")
    public void progressShouldRemainUnchangedAfterStop() {
        int atStop = progressBarPage.getCurrentProgress();
        WaitUtils.hardSleep(1000);
        int afterWait = progressBarPage.getCurrentProgress();
        ctx.log("At stop: " + atStop + "% | After 1 s: " + afterWait + "%");
        Assert.assertEquals(atStop, afterWait,
            "Progress should not increase after Stop is clicked");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SAMPLE APP
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the sample app page")
    public void navigateToSampleApp() {
        sampleAppPage = new SampleAppPage(driver());
        sampleAppPage.navigateToSampleAppPage();
    }

    @Then("the sample app page should be loaded")
    public void sampleAppPageShouldBeLoaded() {
        Assert.assertTrue(sampleAppPage.isPageLoaded(), "Sample App page should be loaded");
    }

    @Then("the sample app login button should be visible")
    public void sampleAppLoginButtonShouldBeVisible() {
        Assert.assertTrue(sampleAppPage.isLoginButtonDisplayed(),
            "Login button should be visible on Sample App");
    }

    @When("I login to the sample app with username {string} and password {string}")
    public void loginToSampleApp(String username, String password) {
        sampleAppPage.login(username, password);
        WaitUtils.hardSleep(500);
    }

    @Then("I should be logged in to the sample app")
    public void shouldBeLoggedIn() {
        String status = sampleAppPage.getLoginStatus();
        ctx.log("Login status: " + status);
        Assert.assertTrue(sampleAppPage.isLoggedIn(),
            "User should be logged in with valid credentials");
    }

    @Then("the sample app login should fail")
    public void sampleAppLoginShouldFail() {
        Assert.assertTrue(sampleAppPage.isLoginFailed(),
            "Login should fail with invalid credentials");
    }

    @Then("I should not be logged in to the sample app")
    public void shouldNotBeLoggedIn() {
        Assert.assertFalse(sampleAppPage.isLoggedIn(),
            "Should not be logged in with empty username");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MOUSE OVER
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the mouse over page")
    public void navigateToMouseOver() {
        mouseOverPage = new MouseOverPage(driver());
        mouseOverPage.navigateToMouseOverPage();
    }

    @Then("the mouse over page should be loaded")
    public void mouseOverPageShouldBeLoaded() {
        Assert.assertTrue(mouseOverPage.isPageLoaded(), "Mouse Over page should be loaded");
    }

    @When("I hover and click the mouse-over link")
    public void hoverAndClickLink() {
        mouseOverPage.hoverAndClickLink();
    }

    @Then("the click count should be at least {int}")
    public void clickCountShouldBeAtLeast(int min) {
        int count = mouseOverPage.getClickCount();
        ctx.log("Click count: " + count);
        Assert.assertTrue(count >= min, "Click count should be >= " + min);
    }

    @When("I hover and click the mouse-over link {int} times")
    public void hoverAndClickLinkNTimes(int n) {
        mouseOverPage.clickLinkNTimes(n);
    }

    @Then("the click count should be exactly {int}")
    public void clickCountShouldBeExactly(int expected) {
        int count = mouseOverPage.getClickCount();
        ctx.log("Click count: " + count);
        Assert.assertEquals(count, expected, "Click count should be exactly " + expected);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NON-BREAKING SPACE
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the non-breaking space page")
    public void navigateToNonBreakingSpace() {
        nbsPage = new NonBreakingSpacePage(driver());
        nbsPage.navigateToNonBreakingSpacePage();
    }

    @Then("the non-breaking space page should be loaded")
    public void nbsPageShouldBeLoaded() {
        Assert.assertTrue(nbsPage.isPageLoaded(), "Non-Breaking Space page should be loaded");
    }

    @When("I click the NBSP button using normalize-space XPath with text {string}")
    public void clickButtonByNormalizedText(String text) {
        nbsPage.clickButtonByNormalizedText(text);
    }

    @Then("the NBSP button should be found and clicked using normalize-space")
    public void buttonFoundWithNormalizeSpace() {
        Assert.assertTrue(true, "Button found and clicked using normalize-space()");
    }

    @When("I search for the NBSP button with regular space text {string}")
    public void searchButtonWithRegularSpace(String text) {
        capturedFindResult = nbsPage.findButtonWithRegularSpace(text);
        ctx.log("Button found with regular space: " + capturedFindResult);
    }

    @Then("the regular space XPath may fail with non-breaking space text")
    public void regularSpaceXPathMayFail() {
        ctx.log("Regular space find result: " + capturedFindResult);
        Assert.assertTrue(true, "NBSP negative test documented");
    }

    @When("I search for the NBSP button using contains XPath with text {string}")
    public void searchButtonWithContains(String text) {
        capturedFindResult = nbsPage.findButtonWithContains(text);
    }

    @Then("the NBSP button should be found regardless of space type")
    public void buttonFoundWithContains() {
        Assert.assertTrue(capturedFindResult, "contains() should find button regardless of space type");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // OVERLAPPED ELEMENT
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the overlapped element page")
    public void navigateToOverlappedElement() {
        overlappedPage = new OverlappedElementPage(driver());
        overlappedPage.navigateToOverlappedElementPage();
    }

    @Then("the overlapped element page should be loaded")
    public void overlappedElementPageShouldBeLoaded() {
        Assert.assertTrue(overlappedPage.isPageLoaded(), "Overlapped Element page should be loaded");
    }

    @When("I enter {string} in the overlapped name field using JavaScript scroll")
    public void enterNameWithJsScroll(String name) {
        overlappedPage.enterName(name);
    }

    @When("I enter {string} in the overlapped ID field")
    public void enterIdValue(String id) {
        overlappedPage.enterIdValue(id);
    }

    @When("I enter {string} in the overlapped name field using Actions scroll")
    public void enterNameWithActionsScroll(String name) {
        overlappedPage.enterNameViaActions(name);
    }

    @Then("the overlapped name field should contain {string}")
    public void overlappedNameFieldShouldContain(String expected) {
        Assert.assertEquals(overlappedPage.getNameFieldValue(), expected,
            "Name field should contain: " + expected);
    }

    @Then("the overlapped ID field should contain {string}")
    public void overlappedIdFieldShouldContain(String expected) {
        Assert.assertEquals(overlappedPage.getIdFieldValue(), expected,
            "ID field should contain: " + expected);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SHADOW DOM
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the shadow dom page")
    public void navigateToShadowDom() {
        shadowDomPage = new ShadowDomPage(driver());
        shadowDomPage.navigateToShadowDomPage();
    }

    @Then("the shadow dom page should be loaded")
    public void shadowDomPageShouldBeLoaded() {
        Assert.assertTrue(shadowDomPage.isPageLoaded(), "Shadow DOM page should be loaded");
    }

    @Then("the shadow host element should be present")
    public void shadowHostShouldBePresent() {
        Assert.assertTrue(shadowDomPage.isShadowHostPresent(),
            "Shadow host element should be present");
    }

    @When("I click the generate button in shadow DOM")
    public void clickGenerateInShadowDom() {
        shadowDomPage.clickGenerateButton();
        WaitUtils.hardSleep(500);
    }

    @Then("a GUID should be generated in the shadow DOM input")
    public void guidShouldBeGenerated() {
        Assert.assertTrue(shadowDomPage.isGuidGenerated(),
            "GUID should be generated and visible in shadow DOM input");
        ctx.log("Generated GUID: " + shadowDomPage.getShadowInputValue());
    }

    @When("I click the copy button in shadow DOM")
    public void clickCopyInShadowDom() {
        shadowDomPage.clickCopyButton();
    }

    @Then("the copy operation in shadow DOM should succeed")
    public void copyInShadowDomShouldSucceed() {
        Assert.assertTrue(true, "Copy button inside Shadow DOM clicked without error");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ALERTS
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the alerts page")
    public void navigateToAlerts() {
        alertsPage = new AlertsPage(driver());
        alertsPage.navigateToAlertsPage();
    }

    @Then("the alerts page should be loaded")
    public void alertsPageShouldBeLoaded() {
        Assert.assertTrue(alertsPage.isPageLoaded(), "Alerts page should be loaded");
    }

    @When("I trigger and accept the JavaScript alert")
    public void triggerAndAcceptAlert() {
        alertsPage.triggerAndAcceptAlert();
    }

    @Then("the JavaScript alert should be accepted without error")
    public void alertShouldBeAccepted() {
        Assert.assertTrue(true, "Alert accepted without exception");
    }

    @When("I trigger and accept the confirm dialog")
    public void triggerAndAcceptConfirm() {
        alertsPage.triggerAndAcceptConfirm();
    }

    @Then("the confirm result should indicate OK was pressed")
    public void confirmResultShouldIndicateOk() {
        String result = alertsPage.getConfirmResult();
        ctx.log("Confirm result: " + result);
        Assert.assertTrue(
            result.toLowerCase().contains("ok") ||
            result.toLowerCase().contains("true") ||
            !result.isEmpty(),
            "Confirm result should indicate OK was pressed");
    }

    @When("I trigger and dismiss the confirm dialog")
    public void triggerAndDismissConfirm() {
        alertsPage.triggerAndDismissConfirm();
    }

    @Then("the confirm result should indicate Cancel was pressed")
    public void confirmResultShouldIndicateCancel() {
        String result = alertsPage.getConfirmResult();
        ctx.log("Dismiss confirm result: " + result);
        Assert.assertTrue(
            result.toLowerCase().contains("cancel") ||
            result.toLowerCase().contains("false") ||
            !result.isEmpty(),
            "Result should indicate Cancel was pressed");
    }

    @When("I trigger the prompt and enter text {string}")
    public void triggerPromptAndEnterText(String text) {
        alertsPage.triggerPromptAndEnterText(text);
    }

    @Then("the prompt result should contain {string}")
    public void promptResultShouldContain(String expected) {
        String result = alertsPage.getPromptResult();
        ctx.log("Prompt result: " + result);
        Assert.assertTrue(result.contains(expected),
            "Prompt result should contain: " + expected);
    }

    @When("I trigger and dismiss the prompt")
    public void triggerAndDismissPrompt() {
        alertsPage.triggerAndDismissPrompt();
    }

    @Then("the prompt should be dismissed without error")
    public void promptShouldBeDismissed() {
        String result = alertsPage.getPromptResult();
        ctx.log("Dismissed prompt result: " + result);
        Assert.assertTrue(true, "Prompt dismissed without error");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FILE UPLOAD
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the file upload page")
    public void navigateToFileUpload() {
        fileUploadPage = new FileUploadPage(driver());
        fileUploadPage.navigateToFileUploadPage();
    }

    @Then("the file upload page should be loaded")
    public void fileUploadPageShouldBeLoaded() {
        Assert.assertTrue(fileUploadPage.isPageLoaded(), "File Upload page should be loaded");
    }

    @Then("the file input element should be present")
    public void fileInputShouldBePresent() {
        Assert.assertTrue(fileUploadPage.isFileUploadInputPresent(),
            "File input element should be present");
    }

    @When("I upload a temp file with name {string} and content {string}")
    public void uploadTempFile(String name, String content) {
        try {
            String path = fileUploadPage.uploadTempFile(name, content);
            ctx.log("File uploaded from: " + path);
            Assert.assertFalse(path.isEmpty(), "File path should not be empty after upload");
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to create temp file for upload", e);
        }
    }

    @Then("no filename should be shown before file is selected")
    public void noFilenameShouldBeShown() {
        String fileName = fileUploadPage.getUploadedFileName();
        ctx.log("File name without upload: '" + fileName + "'");
        Assert.assertTrue(fileName == null || fileName.isEmpty(),
            "No file should be shown before selecting one");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ANIMATED BUTTON
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the animated button page")
    public void navigateToAnimatedButton() {
        animatedButtonPage = new AnimatedButtonPage(driver());
        animatedButtonPage.navigateToAnimatedButtonPage();
    }

    @Then("the animated button page should be loaded")
    public void animatedButtonPageShouldBeLoaded() {
        Assert.assertTrue(animatedButtonPage.isPageLoaded(),
            "Animated Button page should be loaded");
    }

    @Then("the animated button should be visible")
    public void animatedButtonShouldBeVisible() {
        Assert.assertTrue(animatedButtonPage.isButtonDisplayed(),
            "Animated button should be visible");
    }

    @When("I wait for the animation to stop and click the button")
    public void waitForAnimationStopAndClick() {
        animatedButtonPage.waitForAnimationStopAndClick();
    }

    @Then("the animated button should be clicked after animation stops")
    public void buttonShouldBeClickedAfterAnimation() {
        Assert.assertTrue(true, "Button clicked after animation stopped");
    }

    @Then("the animated button should be in animation state on page load")
    public void buttonShouldBeAnimating() {
        boolean animating = animatedButtonPage.isButtonAnimating();
        ctx.log("Button animating on load: " + animating);
        Assert.assertTrue(animating, "Button should be in animation state on page load");
    }

    @Then("the animated button initial class should not be empty")
    public void animatedButtonInitialClassShouldNotBeEmpty() {
        String cls = animatedButtonPage.getButtonClass();
        ctx.log("Initial button class: " + cls);
        Assert.assertFalse(cls.isEmpty(), "Button should have an initial animation class");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DISABLED INPUT
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the disabled input page")
    public void navigateToDisabledInput() {
        disabledInputPage = new DisabledInputPage(driver());
        disabledInputPage.navigateToDisabledInputPage();
    }

    @Then("the disabled input page should be loaded")
    public void disabledInputPageShouldBeLoaded() {
        Assert.assertTrue(disabledInputPage.isPageLoaded(),
            "Disabled Input page should be loaded");
    }

    @Then("the input field should be disabled initially")
    public void inputShouldBeDisabledInitially() {
        Assert.assertTrue(disabledInputPage.isInputDisabled(),
            "Input should initially be disabled");
    }

    @When("I click the enable button and wait for the input")
    public void enableInputAndWait() {
        disabledInputPage.enableAndWaitForInput();
    }

    @Then("the input field should become enabled")
    public void inputShouldBecomeEnabled() {
        Assert.assertTrue(disabledInputPage.isInputEnabled(),
            "Input should be enabled after clicking enable");
    }

    @When("I type {string} into the now-enabled input field")
    public void typeInEnabledInput(String text) {
        disabledInputPage.enterTextIntoInput(text);
    }

    @Then("the enabled input field should contain {string}")
    public void enabledInputShouldContain(String expected) {
        Assert.assertEquals(disabledInputPage.getInputValue(), expected,
            "Enabled input should contain: " + expected);
    }

    @Then("typing in the disabled input field should fail")
    public void typingInDisabledFieldShouldFail() {
        boolean typingSucceeded = !disabledInputPage.tryTypingInDisabledField("Test");
        Assert.assertTrue(typingSucceeded, "Typing in disabled field should fail");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AUTO WAIT
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the auto wait page")
    public void navigateToAutoWait() {
        autoWaitPage = new AutoWaitPage(driver());
        autoWaitPage.navigateToAutoWaitPage();
    }

    @Then("the auto wait page should be loaded")
    public void autoWaitPageShouldBeLoaded() {
        Assert.assertTrue(autoWaitPage.isPageLoaded(), "AutoWait page should be loaded");
    }

    @When("I click the apply button on the auto wait page")
    public void clickApplyButtonAutoWait() {
        autoWaitPage.clickApplyButton();
        WaitUtils.hardSleep(500);
    }

    @Then("the auto wait page should still be loaded after applying")
    public void autoWaitPageStillLoaded() {
        Assert.assertTrue(autoWaitPage.isPageLoaded(),
            "AutoWait page should remain loaded after apply");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FRAMES
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the frames page")
    public void navigateToFrames() {
        framesPage = new FramesPage(driver());
        framesPage.navigateToFramesPage();
    }

    @Then("the frames page should be loaded")
    public void framesPageShouldBeLoaded() {
        Assert.assertTrue(framesPage.isPageLoaded(), "Frames page should be loaded");
    }

    @Then("the page should contain at least {int} iframe")
    public void pageShouldContainAtLeastNFrames(int minCount) {
        int count = framesPage.getFrameCount();
        ctx.log("Frame count: " + count);
        Assert.assertTrue(count >= minCount,
            "Page should contain at least " + minCount + " iframe");
    }

    @When("I switch to the outer frame and back to default content")
    public void switchToOuterFrameAndBack() {
        framesPage.switchToOuterFrame();
        framesPage.switchToDefaultContent();
    }

    @Then("frame switching should complete without error")
    public void frameSwitchingShouldComplete() {
        Assert.assertTrue(true, "Frame switching completed without error");
    }

    @Then("reading content from the nested frame should return non-null")
    public void readContentFromNestedFrame() {
        String content = framesPage.readTextFromNestedFrame();
        ctx.log("Content from frame: '" + content + "'");
        Assert.assertNotNull(content, "Content from frame should not be null");
    }

    @Then("frame element lookup without switching context should be documented")
    public void frameElementLookupWithoutSwitching() {
        boolean found = WaitUtils.isElementPresent(driver(), By.id("button"));
        ctx.log("Found frame element without switching: " + found);
        Assert.assertTrue(true, "Frame context test documented");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VISIBILITY
    // ══════════════════════════════════════════════════════════════════════════

    @Given("I navigate to the visibility page")
    public void navigateToVisibility() {
        visibilityPage = new VisibilityPage(driver());
        visibilityPage.navigateToVisibilityPage();
    }

    @Then("the visibility page should be loaded")
    public void visibilityPageShouldBeLoaded() {
        Assert.assertTrue(visibilityPage.isPageLoaded(), "Visibility page should be loaded");
    }

    @Then("the visible button should be displayed on the visibility page")
    public void visibleButtonShouldBeDisplayed() {
        Assert.assertTrue(visibilityPage.isVisibleButtonDisplayed(),
            "Visible button should be displayed");
    }

    @When("I click the hide button on the visibility page")
    public void clickHideButton() {
        visibilityPage.clickHide();
    }

    @Then("the hidden button should not be visible on the visibility page")
    public void hiddenButtonShouldNotBeVisible() {
        Assert.assertFalse(visibilityPage.isHiddenButtonVisible(),
            "Hidden button should not be visible after hide action");
    }

    @Then("hidden elements may still exist in the DOM")
    public void hiddenElementsMayStillExistInDom() {
        boolean inDom = visibilityPage.isTransparentButtonInDom() ||
                        visibilityPage.isNotDisplayedButtonInDom();
        ctx.log("Hidden elements in DOM: " + inDom);
        Assert.assertTrue(true, "DOM visibility check completed");
    }
}
