package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FileUploadPage - Upload files using Selenium.
 * Use sendKeys on the file input element (no need to interact with OS dialog).
 */
public class FileUploadPage extends BasePage {

    private final By fileInput = By.cssSelector("input[type='file']");
    private final By uploadedFileLabel = By.id("uploadedFilePath");
    private final By uploadButton = By.cssSelector("input[type='submit']");

    public FileUploadPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToFileUploadPage() {
        navigateToPage("/fileupload");
    }

    /**
     * Upload a file by sending the absolute path to the file input element.
     * This bypasses the OS file dialog entirely.
     */
    public void uploadFile(String absoluteFilePath) {
        logger.info("Uploading file: {}", absoluteFilePath);
        WebElement input = WaitUtils.waitForPresence(driver, fileInput);
        input.sendKeys(absoluteFilePath);
    }

    /**
     * Upload a temporary test file created on the fly
     */
    public String uploadTempFile(String fileName, String content) throws IOException {
        Path tempFile = Files.createTempFile(fileName.replace(".", "_"), ".txt");
        Files.writeString(tempFile, content);
        String absolutePath = tempFile.toAbsolutePath().toString();
        uploadFile(absolutePath);
        return absolutePath;
    }

    public void clickUploadButton() {
        try {
            WaitUtils.waitForClickable(driver, uploadButton).click();
        } catch (Exception e) {
            logger.info("No submit button found, file input may auto-submit");
        }
    }

    public String getUploadedFileName() {
        try {
            return WaitUtils.waitForVisible(driver, uploadedFileLabel, 5).getText();
        } catch (Exception e) {
            // Try getting value from input
            try {
                return driver.findElement(fileInput).getAttribute("value");
            } catch (Exception ex) {
                return "";
            }
        }
    }

    public boolean isFileUploadInputPresent() {
        return WaitUtils.isElementPresent(driver, fileInput);
    }

    public boolean isPageLoaded() {
        return isDisplayed(fileInput);
    }
}
