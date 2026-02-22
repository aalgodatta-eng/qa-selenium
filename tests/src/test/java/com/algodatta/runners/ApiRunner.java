package com.algodatta.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
  features = "src/test/resources/features/api",
  glue = {"ui.steps.api", "com.algodatta.hooks"},
  plugin = {
    "pretty",
    "json:target/cucumber/api-cucumber.json",
    "html:target/cucumber/api-cucumber.html",
    "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
    "com.epam.reportportal.cucumber.ScenarioReporter"
  },
  monochrome = true
)
public class ApiRunner extends ParallelRunnerBase {}
