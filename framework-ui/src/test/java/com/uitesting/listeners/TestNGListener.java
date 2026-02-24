package com.uitesting.listeners;

import com.aventstack.extentreports.Status;
import com.uitesting.utils.ExtentReportManager;
import com.uitesting.utils.ScreenshotUtils;
import com.uitesting.utils.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * TestNGListener - Hooks into TestNG lifecycle for enhanced logging and reporting
 */
public class TestNGListener implements ITestListener, ISuiteListener {

    private static final Logger logger = LoggerFactory.getLogger(TestNGListener.class);

    @Override
    public void onStart(ISuite suite) {
        logger.info("╔══════════════════════════════════════╗");
        logger.info("║  Suite Starting: {}  ║", suite.getName());
        logger.info("╚══════════════════════════════════════╝");
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flushReports();
        logger.info("╔══════════════════════════════════════╗");
        logger.info("║  Suite Finished: {}  ║", suite.getName());
        logger.info("╚══════════════════════════════════════╝");
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("▶ Starting: [{}] {}", result.getTestClass().getName(), result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("✅ PASSED: {}", result.getName());
        var test = ExtentReportManager.getTest();
        if (test != null) test.log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("❌ FAILED: {} - {}", result.getName(), result.getThrowable().getMessage());
        try {
            var driver = DriverManager.getDriver();
            if (driver != null) {
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());
                var test = ExtentReportManager.getTest();
                if (test != null && screenshotPath != null) {
                    test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                }
            }
        } catch (Exception e) {
            logger.warn("Could not capture screenshot on failure: {}", e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("⚠️ SKIPPED: {}", result.getName());
        var test = ExtentReportManager.getTest();
        if (test != null) test.log(Status.SKIP, "Test skipped");
    }
}
