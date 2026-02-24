package com.algodatta.hooks;

import com.algodatta.context.ScenarioContext;
import com.algodatta.core.driver.DriverFactory;
import com.algodatta.core.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class Hooks {

  private final ScenarioContext ctx;

  public Hooks(ScenarioContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Registers the live Cucumber {@link Scenario} into {@link ScenarioContext} so that
   * step definitions and other hooks can call {@code ctx.log()} / {@code ctx.attach()}.
   * Must run first (order = 0) and applies to ALL scenarios (API + UI).
   */
  @Before(order = 0)
  public void registerScenario(Scenario scenario) {
    ctx.setScenario(scenario);
  }

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

        // Attach to Cucumber scenario → picked up by Extent CucumberAdapter + Allure
        scenario.attach(bytes, "image/png", "Failure Screenshot");

        // Save to disk for the Extent screenshot thumbnail.
        // project.build.directory is injected by Surefire's systemPropertyVariables
        // so it resolves to the actual build dir (e.g. target_20260224_175652).
        String buildDir = System.getProperty("project.build.directory", "target");
        Path screenshotDir = Path.of(buildDir, "extent", "screenshots");
        Files.createDirectories(screenshotDir);
        Files.write(screenshotDir.resolve(safeName(scenario.getName()) + ".png"), bytes);

        // Push screenshot image to ReportPortal (only when RP is enabled).
        // Default is false (overridden via -Drp.enable=true or reportportal.properties).
        // Wrapped in try/catch — RP is optional infrastructure.
        if (Boolean.parseBoolean(System.getProperty("rp.enable", "false"))) {
          try {
            File tmpFile = File.createTempFile("rp-screenshot-", ".png");
            tmpFile.deleteOnExit();
            Files.write(tmpFile.toPath(), bytes);
            com.epam.reportportal.service.ReportPortal.emitLog(
                "Failure Screenshot: " + scenario.getName(),
                com.epam.reportportal.listeners.LogLevel.ERROR.name(),
                new Date(),
                tmpFile
            );
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
