package com.algodatta.core.driver;

import com.algodatta.core.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.net.URL;

public final class DriverFactory {
  private DriverFactory() {}

  public static void initDriver() {
    String runMode = val("runMode", "local");
    String browser = val("browser", "chrome");
    boolean headless = bool("headless", false);

    WebDriver driver = "grid".equalsIgnoreCase(runMode)
        ? createRemote(browser, headless, val("gridUrl", ""))
        : createLocal(browser, headless);

    DriverManager.setDriver(driver);
  }

  private static WebDriver createLocal(String browser, boolean headless) {
    return switch (browser.toLowerCase()) {
      case "firefox" -> { WebDriverManager.firefoxdriver().setup(); yield new org.openqa.selenium.firefox.FirefoxDriver(buildFirefoxOptions(headless)); }
      case "edge" -> { WebDriverManager.edgedriver().setup(); yield new org.openqa.selenium.edge.EdgeDriver(buildEdgeOptions(headless)); }
      default -> { WebDriverManager.chromedriver().setup(); yield new org.openqa.selenium.chrome.ChromeDriver(buildChromeOptions(headless)); }
    };
  }

  private static WebDriver createRemote(String browser, boolean headless, String gridUrl) {
  if (gridUrl == null || gridUrl.isBlank()) throw new IllegalArgumentException("gridUrl is required when runMode=grid");
  try {
    String browserVersion = val("browserVersion", "");
    String platformName = val("platformName", "");
    boolean enableVnc = bool("enableVnc", true);
    boolean enableVideo = bool("enableVideo", false);
    String videoName = val("videoName", ""); // optional override

    MutableCapabilities caps = switch (browser.toLowerCase()) {
      case "firefox" -> buildFirefoxOptions(headless);
      case "edge" -> buildEdgeOptions(headless);
      default -> buildChromeOptions(headless);
    };

    if (!browserVersion.isBlank()) caps.setCapability("browserVersion", browserVersion);
    if (!platformName.isBlank()) caps.setCapability("platformName", platformName);

    // Selenium Grid (v4) VNC capability (works with some node images / integrations)
    caps.setCapability("se:vncEnabled", enableVnc);

    // Selenoid options (Grid ignores unknown caps)
    java.util.Map<String, Object> selenoid = new java.util.HashMap<>();
    selenoid.put("enableVNC", enableVnc);
    selenoid.put("enableVideo", enableVideo);
    selenoid.put("screenResolution", val("screenResolution", "1920x1080x24"));
    if (!videoName.isBlank()) selenoid.put("videoName", videoName);
    caps.setCapability("selenoid:options", selenoid);

    return new RemoteWebDriver(URI.create(gridUrl).toURL(), caps);
  } catch (Exception e) {
    throw new RuntimeException("Failed to create RemoteWebDriver", e);
  }
}


  private static ChromeOptions buildChromeOptions(boolean headless) {
    ChromeOptions o = new ChromeOptions();
    if (headless) o.addArguments("--headless=new");
    o.addArguments("--no-sandbox", "--disable-dev-shm-usage");
    o.addArguments("--window-size=1920,1080");
    return o;
  }

  private static FirefoxOptions buildFirefoxOptions(boolean headless) {
    FirefoxOptions o = new FirefoxOptions();
    if (headless) o.addArguments("-headless");
    return o;
  }

  private static EdgeOptions buildEdgeOptions(boolean headless) {
    EdgeOptions o = new EdgeOptions();
    if (headless) o.addArguments("--headless=new");
    o.addArguments("--window-size=1920,1080");
    return o;
  }

  private static String val(String key, String def) {
    String v = ConfigManager.get(key);
    return v == null ? def : v;
  }

  private static boolean bool(String key, boolean def) {
    return ConfigManager.getBoolean(key, def);
  }
}
