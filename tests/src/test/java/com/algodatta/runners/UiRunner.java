package com.algodatta.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
  features = "src/test/resources/features/ui",
  glue     = {"ui.steps.ui", "com.algodatta.hooks"},
  plugin   = {
    "pretty",
    // Allure: generates per-scenario JSON files for Allure HTML report
    // Gives Feature > Scenario > Step hierarchy (better than allure-testng alone)
    "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
    // Extent: live HTML report
    "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
    // Raw JSON + HTML output
    "json:target/cucumber/ui-cucumber.json",
    "html:target/cucumber/ui-cucumber.html",
    // ReportPortal (disabled by default via -Drp.enable=false)
    "com.epam.reportportal.cucumber.ScenarioReporter"
  },
  monochrome = true
)
public class UiRunner extends ParallelRunnerBase {}
