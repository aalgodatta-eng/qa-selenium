package com.algodatta.hooks;

import com.algodatta.core.driver.DriverFactory;
import com.algodatta.core.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.epam.reportportal.service.ReportPortal;
import com.epam.reportportal.message.ReportPortalMessage;
import com.epam.reportportal.utils.files.Utils;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import com.epam.reportportal.listeners.LogLevel;
import io.reactivex.Maybe;

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
        byte[] bytes = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
        scenario.attach(bytes, "image/png", "Failure Screenshot");

        Path out = Path.of("target", "extent", "screenshots");
        Files.createDirectories(out);
        Files.write(out.resolve(safeName(scenario.getName()) + ".png"), bytes);

        // Push attachment to ReportPortal (if enabled)
            try {
              if (Boolean.parseBoolean(System.getProperty("rp.enable", "true"))) {
                ReportPortal.emitLog(itemUuid -> {
                  SaveLogRQ saveLogRQ = new SaveLogRQ();
                  saveLogRQ.setMessage("Failure Screenshot");
                  saveLogRQ.setLevel(LogLevel.ERROR.name());
                  saveLogRQ.setLogTime(System.currentTimeMillis());
                  return saveLogRQ;
                });
              }
            } catch (Exception ignored2) {
            }
      }
    } catch (Exception ignored) {
    } finally {
      DriverManager.quit();
    }
  }

  private static String safeName(String s) {
    return s.replaceAll("[^a-zA-Z0-9-_]+", "_");
  }
}
