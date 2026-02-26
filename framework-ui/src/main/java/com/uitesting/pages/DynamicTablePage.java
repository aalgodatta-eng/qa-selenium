package com.uitesting.pages;

import com.uitesting.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * DynamicTablePage - Verify cell values in a table that is regenerated dynamically.
 * Table structure can change between loads; must find rows dynamically.
 */
public class DynamicTablePage extends BasePage {

    // The warning label at top shows Chrome's CPU value for comparison
    private final By warningLabel = By.cssSelector("p.bg-warning");

    // Table structure changes dynamically
    private final By tableRows = By.cssSelector("div[role='row']");
    private final By tableHeaders = By.cssSelector("div[role='columnheader']");

    public DynamicTablePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToDynamicTablePage() {
        navigateToPage("/dynamictable");
    }

    /**
     * Get the CPU value shown in the warning label (reference value)
     */
    public String getCpuValueFromWarning() {
        String warningText = getText(warningLabel);
        logger.info("Warning text: {}", warningText);
        // Format: "Chrome CPU: XX.XX%"
        String[] parts = warningText.split(":");
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Find Chrome's CPU value dynamically from the table
     * Must handle dynamic column ordering
     */
    public String getChromeCpuFromTable() {
        List<WebElement> rows = driver.findElements(tableRows);

        // Find header row to determine column index of "CPU"
        WebElement headerRow = rows.getFirst();
        List<WebElement> headers = headerRow.findElements(By.cssSelector("div[role='columnheader']"));
        int cpuColumnIndex = -1;

        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().equalsIgnoreCase("CPU")) {
                cpuColumnIndex = i;
                break;
            }
        }

        if (cpuColumnIndex == -1) {
            logger.error("CPU column not found in table headers");
            return "";
        }

        // Find Chrome row and get CPU value at the identified index
        for (int i = 1; i < rows.size(); i++) {
            List<WebElement> cells = rows.get(i).findElements(By.cssSelector("div[role='cell']"));
            for (WebElement cell : cells) {
                if (cell.getText().trim().equalsIgnoreCase("Chrome")) {
                    if (cpuColumnIndex < cells.size()) {
                        return cells.get(cpuColumnIndex).getText().trim();
                    }
                }
            }
        }

        return "";
    }

    /**
     * Verify table CPU value matches warning label value
     */
    public boolean isTableValueMatchingWarning() {
        String warningCpu = getCpuValueFromWarning();
        String tableCpu = getChromeCpuFromTable();
        logger.info("Warning CPU: '{}', Table CPU: '{}'", warningCpu, tableCpu);
        return warningCpu.equalsIgnoreCase(tableCpu);
    }

    public int getRowCount() {
        return driver.findElements(tableRows).size() - 1; // Exclude header
    }

    public boolean isPageLoaded() {
        return isDisplayed(warningLabel) && !driver.findElements(tableRows).isEmpty();
    }
}
