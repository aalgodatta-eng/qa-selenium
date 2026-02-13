package com.algodatta.ui.base;

import com.algodatta.core.driver.DriverFactory;
import com.algodatta.core.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
  @BeforeMethod(alwaysRun = true)
  public void setUp() { DriverFactory.initDriver(); }

  @AfterMethod(alwaysRun = true)
  public void tearDown() { DriverManager.quit(); }
}
