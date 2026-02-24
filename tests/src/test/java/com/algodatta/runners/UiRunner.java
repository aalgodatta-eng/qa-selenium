package com.algodatta.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
  features = "classpath:features/ui",
  glue     = {"ui.steps.ui", "com.algodatta.hooks"},
  plugin   = {
    "pretty",
    // Allure: generates per-scenario JSON files for Allure HTML report
    // Gives Feature > Scenario > Step hierarchy (better than allure-testng alone)
    "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
    // Extent: live HTML report
    "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
    // Raw JSON + HTML output
    // Paths are relative to Surefire workingDirectory = ${project.build.directory}
    "json:cucumber/ui-cucumber.json",
    "html:cucumber/ui-cucumber.html",
    // ReportPortal (disabled by default via -Drp.enable=false)
    "com.epam.reportportal.cucumber.ScenarioReporter"
  },
  monochrome = true
)
public class UiRunner extends ParallelRunnerBase {}
