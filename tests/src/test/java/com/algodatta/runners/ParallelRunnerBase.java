package com.algodatta.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.DataProvider;

/**
 * Enables scenario-level parallel execution.
 * Thread count is controlled via surefire TestNG property 'dataproviderthreadcount'
 * (wired to -Dthreads in tests/pom.xml).
 */
public abstract class ParallelRunnerBase extends AbstractTestNGCucumberTests {

  @Override
  @DataProvider(parallel = true)
  public Object[][] scenarios() {
    return super.scenarios();
  }
}
