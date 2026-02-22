package com.algodatta.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
  features = "src/test/resources/features/api",
  glue     = {"ui.steps.api", "com.algodatta.hooks"},
  plugin   = {
    "pretty",
    // Allure: generates per-scenario JSON files for Allure HTML report
    "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
    // Extent: live HTML report
    "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
    // Raw JSON + HTML output
    "json:target/cucumber/api-cucumber.json",
    "html:target/cucumber/api-cucumber.html",
    // ReportPortal (disabled by default via -Drp.enable=false)
    "com.epam.reportportal.cucumber.ScenarioReporter"
  },
  monochrome = true
)
public class ApiRunner extends ParallelRunnerBase {}
