package com.uitesting.pages;

import com.uitesting.utils.JSUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * ShadowDomPage - Interact with elements inside Shadow DOM.
 * Standard Selenium locators don't work inside Shadow DOM.
 * Must use JavaScript to pierce the shadow root.
 */
public class ShadowDomPage extends BasePage {

    // The host element that contains the shadow root
    private final By shadowHostLocator = By.cssSelector("guid-generator");

    public ShadowDomPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToShadowDomPage() {
        navigateToPage("/shadowdom");
    }

    /**
     * Get shadow root element using Selenium 4 native API
     */
    public SearchContext getShadowRoot() {
        WebElement shadowHost = driver.findElement(shadowHostLocator);
        return shadowHost.getShadowRoot();
    }

    /**
     * Get element inside shadow DOM using Selenium 4 shadow root
     */
    public WebElement findInShadowDom(String cssSelector) {
        SearchContext shadowRoot = getShadowRoot();
        return shadowRoot.findElement(By.cssSelector(cssSelector));
    }

    /**
     * Get shadow DOM input value
     */
    public String getShadowInputValue() {
        try {
            WebElement input = findInShadowDom("input");
            return input.getAttribute("value");
        } catch (Exception e) {
            // Fallback: use JavaScript to pierce shadow DOM
            return (String) JSUtils.executeScript(driver,
                "return document.querySelector('guid-generator').shadowRoot.querySelector('input').value;");
        }
    }

    /**
     * Click Generate button inside shadow DOM
     */
    public void clickGenerateButton() {
        logger.info("Clicking Generate button inside Shadow DOM");
        try {
            WebElement btn = findInShadowDom("#buttonGenerate");
            btn.click();
        } catch (Exception e) {
            JSUtils.executeScript(driver,
                "document.querySelector('guid-generator').shadowRoot.querySelector('#buttonGenerate').click();");
        }
    }

    /**
     * Click Copy button inside shadow DOM
     */
    public void clickCopyButton() {
        logger.info("Clicking Copy button inside Shadow DOM");
        try {
            WebElement btn = findInShadowDom("#buttonCopy");
            btn.click();
        } catch (Exception e) {
            JSUtils.executeScript(driver,
                "document.querySelector('guid-generator').shadowRoot.querySelector('#buttonCopy').click();");
        }
    }

    /**
     * Verify GUID was generated (non-empty value)
     */
    public boolean isGuidGenerated() {
        String value = getShadowInputValue();
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Verify shadow host is present on page
     */
    public boolean isShadowHostPresent() {
        return !driver.findElements(shadowHostLocator).isEmpty();
    }

    public boolean isPageLoaded() {
        return isShadowHostPresent();
    }
}
