# UITestingPlayground - Selenium Automation Suite

A professional, production-ready **Selenium + Java + TestNG** automation framework for
[http://uitestingplayground.com](http://uitestingplayground.com), featuring:

- ✅ **Smoke Suite** — 20 fast page-load sanity checks
- ✅ **Regression Suite** — 60+ positive & negative tests across all 25 features
- ✅ **Page Object Model (POM)** architecture
- ✅ **ThreadLocal WebDriver** (parallel execution ready)
- ✅ **ExtentReports** HTML reporting with screenshots on failure
- ✅ **WebDriverManager** (no manual driver downloads needed)
- ✅ **Log4j2** structured logging

---

## 📁 Project Structure

```
uitestingplayground-automation/
├── pom.xml
├── testng-suites/
│   ├── smoke-suite.xml              ← Fast sanity tests (20 tests)
│   └── regression-suite.xml        ← Full test suite (80+ tests)
├── src/
│   ├── main/java/com/uitesting/
│   │   ├── config/
│   │   │   └── ConfigManager.java  ← Config from properties/system props
│   │   ├── pages/                  ← Page Object Model classes
│   │   │   ├── BasePage.java
│   │   │   ├── HomePage.java
│   │   │   ├── DynamicIdPage.java
│   │   │   ├── ClassAttributePage.java
│   │   │   ├── HiddenLayersPage.java
│   │   │   ├── LoadDelayPage.java
│   │   │   ├── AjaxDataPage.java
│   │   │   ├── ClientSideDelayPage.java
│   │   │   ├── ClickPage.java
│   │   │   ├── TextInputPage.java
│   │   │   ├── ScrollbarsPage.java
│   │   │   ├── DynamicTablePage.java
│   │   │   ├── VerifyTextPage.java
│   │   │   ├── ProgressBarPage.java
│   │   │   ├── VisibilityPage.java
│   │   │   ├── SampleAppPage.java
│   │   │   ├── MouseOverPage.java
│   │   │   ├── NonBreakingSpacePage.java
│   │   │   ├── OverlappedElementPage.java
│   │   │   ├── ShadowDomPage.java
│   │   │   ├── AlertsPage.java
│   │   │   ├── FileUploadPage.java
│   │   │   ├── AnimatedButtonPage.java
│   │   │   ├── DisabledInputPage.java
│   │   │   ├── AutoWaitPage.java
│   │   │   └── FramesPage.java
│   │   └── utils/
│   │       ├── DriverManager.java      ← ThreadLocal WebDriver
│   │       ├── WaitUtils.java          ← Explicit/fluent waits
│   │       ├── JSUtils.java            ← JavaScript execution helpers
│   │       ├── ScreenshotUtils.java    ← Failure screenshots
│   │       └── ExtentReportManager.java← HTML test reports
│   └── test/java/com/uitesting/
│       ├── BaseTest.java              ← @Before/@After setup/teardown
│       ├── listeners/
│       │   └── TestNGListener.java
│       ├── smoke/
│       │   └── SmokeTests.java        ← 20 smoke tests
│       └── regression/
│           ├── RegressionTests.java   ← DynamicID → Click (20 tests)
│           ├── RegressionTests2.java  ← TextInput → ProgressBar (20 tests)
│           └── RegressionTests3.java  ← Visibility → Frames (25 tests)
└── src/test/resources/
    ├── config.properties
    └── log4j2.xml
```

---

## 🚀 How to Run

### Prerequisites
- Java 11+
- Maven 3.6+
- Chrome browser installed (WebDriverManager handles the driver)

### Run Smoke Suite
```bash
mvn test -Psmoke
```

### Run Regression Suite (includes smoke)
```bash
mvn test -Pregression
```

### Run with Headless Chrome
```bash
mvn test -Psmoke -Dheadless=true
mvn test -Pregression -Dheadless=true
```

### Run with Firefox
```bash
mvn test -Psmoke -Dbrowser=firefox
```

### Run specific test group
```bash
mvn test -Pregression -Dgroups=ajax,click,alerts
```

### Run via TestNG XML directly
```bash
mvn test -DsuiteFile=testng-suites/smoke-suite.xml
mvn test -DsuiteFile=testng-suites/regression-suite.xml
```

---

## 📊 Test Coverage

| # | Feature | Smoke | Regression+ | Negative |
|---|---------|-------|-------------|----------|
| 1 | Dynamic ID | ✅ | ✅ ✅ ✅ | ✅ |
| 2 | Class Attribute | ✅ | ✅ ✅ ✅ | - |
| 3 | Hidden Layers | ✅ | ✅ ✅ | ✅ |
| 4 | Load Delay | ✅ | ✅ ✅ | ✅ |
| 5 | AJAX Data | ✅ | ✅ ✅ | ✅ |
| 6 | Client Side Delay | ✅ | ✅ ✅ | ✅ |
| 8 | Click | ✅ | ✅ ✅ | ✅ |
| 9 | Text Input | ✅ | ✅ ✅ ✅ ✅ ✅ | ✅ |
| 10 | Scrollbars | ✅ | ✅ ✅ ✅ | - |
| 11 | Dynamic Table | ✅ | ✅ ✅ ✅ ✅ | ✅ |
| 12 | Verify Text | ✅ | ✅ ✅ | ✅ |
| 13 | Progress Bar | ✅ | ✅ ✅ ✅ | ✅ |
| 14 | Visibility | - | ✅ ✅ | ✅ |
| 15 | Sample App | ✅ | ✅ ✅ | ✅ ✅ |
| 16 | Mouse Over | - | ✅ ✅ | - |
| 17 | Non-Breaking Space | - | ✅ | ✅ |
| 18 | Overlapped Element | - | ✅ ✅ ✅ | - |
| 19 | Shadow DOM | ✅ | ✅ ✅ ✅ | - |
| 20 | Alerts | ✅ | ✅ ✅ ✅ ✅ | ✅ ✅ |
| 21 | File Upload | ✅ | ✅ ✅ | ✅ |
| 22 | Animated Button | ✅ | ✅ ✅ ✅ | - |
| 23 | Disabled Input | ✅ | ✅ ✅ | ✅ |
| 24 | Auto Wait | - | ✅ | - |
| 25 | Frames | ✅ | ✅ ✅ ✅ | ✅ |

**Total: ~80 test cases** across smoke and regression suites

---

## 🔑 Key Design Decisions

### 1. Dynamic ID — Stable Selectors Only
```java
// ❌ WRONG: ID changes on every page load
By.id("RWUVAIEWQNZ")

// ✅ CORRECT: Use class, name, or text
By.cssSelector("button.btn-primary")
By.xpath("//button[contains(@class,'btn-primary')]")
```

### 2. Class Attribute — contains() in XPath
```java
// ❌ WRONG: Exact match breaks with multiple classes
By.xpath("//button[@class='btn btn-primary']")

// ✅ CORRECT: contains() handles multi-class attributes
By.xpath("//button[contains(@class,'btn-primary')]")
```

### 3. Waits — Explicit over Implicit
```java
// ❌ WRONG: Fixed sleep
Thread.sleep(5000);

// ✅ CORRECT: Explicit wait until condition
WaitUtils.waitForVisible(driver, locator, 15);
WaitUtils.waitForClickable(driver, locator);
```

### 4. Click — Standard over JS
```java
// ❌ WRONG for event-driven buttons: JS click skips event listeners
js.executeScript("arguments[0].click()", element);

// ✅ CORRECT: WebDriver click fires all event listeners
element.click();
new Actions(driver).click(element).perform();
```

### 5. Shadow DOM — Selenium 4 Native API
```java
// ✅ Use shadow root via Selenium 4
SearchContext shadowRoot = hostElement.getShadowRoot();
WebElement innerEl = shadowRoot.findElement(By.cssSelector("#target"));
```

### 6. NBSP — normalize-space() in XPath
```java
// ❌ May fail: regular space vs non-breaking space (\u00A0)
By.xpath("//button[text()='My Button']")

// ✅ CORRECT: normalize-space() handles both
By.xpath("//button[normalize-space()='My Button']")
```

---

## 📈 Reports & Logs

After running tests:
- **HTML Report**: `test-output/reports/TestReport_<timestamp>.html`
- **Screenshots**: `test-output/screenshots/` (on failures)
- **Log file**: `test-output/logs/automation.log`
