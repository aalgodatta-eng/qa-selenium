package com.algodatta.hooks;

import com.algodatta.core.driver.DriverFactory;
import com.algodatta.core.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.nio.file.Files;
import java.nio.file.Path;

public class Hooks {

  @Before("@ui")
  public void beforeUi() {
    DriverFactory.initDriver();
  }

  @After("@ui")
  public void afterUi(Scenario scenario) {
    try {
      if (scenario.isFailed() && DriverManager.getDriver() != null) {
        byte[] bytes = ((TakesScreenshot) DriverManager.getDriver())
            .getScreenshotAs(OutputType.BYTES);
        scenario.attach(bytes, "image/png", "Failure Screenshot");

        Path out = Path.of("target", "extent", "screenshots");
        Files.createDirectories(out);
        Files.write(out.resolve(safeName(scenario.getName()) + ".png"), bytes);

        // Push screenshot to ReportPortal only when explicitly enabled.
        // Default is false (overridden via -Drp.enable=true or reportportal.properties).
        // Wrapped in try/catch — RP is optional infrastructure.
        if (Boolean.parseBoolean(System.getProperty("rp.enable", "false"))) {
          try {
            com.epam.reportportal.service.ReportPortal.emitLog(itemUuid -> {
              com.epam.ta.reportportal.ws.model.log.SaveLogRQ rq =
                  new com.epam.ta.reportportal.ws.model.log.SaveLogRQ();
              rq.setMessage("Failure Screenshot");
              rq.setLevel(com.epam.reportportal.listeners.LogLevel.ERROR.name());
              rq.setLogTime(System.currentTimeMillis());
              return rq;
            });
          } catch (Exception ignored) { /* RP not configured — silently skip */ }
        }
      }
    } catch (Exception ignored) {
      // Screenshot capture is best-effort — never fail the test on this
    } finally {
      DriverManager.quit();
    }
  }

  private static String safeName(String s) {
    return s.replaceAll("[^a-zA-Z0-9-_]+", "_");
  }
}
