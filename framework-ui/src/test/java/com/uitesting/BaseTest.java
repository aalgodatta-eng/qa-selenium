package com.uitesting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.uitesting.config.ConfigManager;
import com.uitesting.utils.DriverManager;
import com.uitesting.utils.ExtentReportManager;
import com.uitesting.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * BaseTest - Parent class for all test classes.
 * Handles driver lifecycle, reporting, and screenshot capture.
 */
public abstract class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected ConfigManager config;
    protected String baseUrl;

    @BeforeSuite
    public void setUpSuite() {
        logger.info("=== Test Suite Starting ===");
        ExtentReportManager.getInstance(); // Initialize report
    }

    @BeforeMethod
    public void setUp(Method method) {
        config = ConfigManager.getInstance();
        baseUrl = config.getBaseUrl();

        // Initialize WebDriver
        DriverManager.initDriver();
        driver = DriverManager.getDriver();

        // Create test entry in extent report
        String testName = method.getName();
        String description = getTestDescription(method);
        ExtentTest test = ExtentReportManager.createTest(testName, description);
        test.log(Status.INFO, "Test started: " + testName);

        logger.info("========== Starting test: {} ==========", testName);

        // Navigate to base URL
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();

        if (result.getStatus() == ITestResult.FAILURE) {
            // Capture screenshot on failure
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());
            if (screenshotPath != null && test != null) {
                try {
                    test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                } catch (Exception e) {
                    logger.warn("Could not attach screenshot to report: {}", e.getMessage());
                }
            }
            if (test != null) {
                test.log(Status.FAIL, "TEST FAILED: " + result.getThrowable().getMessage());
                test.fail(result.getThrowable());
            }
            logger.error("TEST FAILED: {}", result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            if (test != null) test.log(Status.SKIP, "Test skipped");
            logger.warn("TEST SKIPPED: {}", result.getName());
        } else {
            if (test != null) test.log(Status.PASS, "TEST PASSED");
            logger.info("TEST PASSED: {}", result.getName());
        }

        logger.info("========== Ending test: {} ==========", result.getName());

        // Quit driver
        DriverManager.quitDriver();
    }

    @AfterSuite
    public void tearDownSuite() {
        ExtentReportManager.flushReports();
        logger.info("=== Test Suite Completed. Report: {} ===",
            ExtentReportManager.getReportPath());
    }

    private String getTestDescription(Method method) {
        org.testng.annotations.Test testAnnotation = method.getAnnotation(org.testng.annotations.Test.class);
        if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
            return testAnnotation.description();
        }
        return "Test: " + method.getName();
    }
}
