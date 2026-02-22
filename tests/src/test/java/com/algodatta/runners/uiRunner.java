package com.algodatta.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
  features = "src/test/resources/features/ui",
        glue = {"ui.steps.ui", "com.algodatta.hooks"},
  plugin = {
    "pretty",
    "json:target/cucumber/ui-cucumber.json",
    "html:target/cucumber/ui-cucumber.html",
    "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
    "com.epam.reportportal.cucumber.ScenarioReporter"
  },
  monochrome = true
)
public class uiRunner extends ParallelRunnerBase {}
