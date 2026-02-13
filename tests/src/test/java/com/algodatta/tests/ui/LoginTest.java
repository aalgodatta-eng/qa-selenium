package com.algodatta.tests.ui;

import com.algodatta.core.config.ConfigManager;
import com.algodatta.core.driver.DriverManager;
import com.algodatta.ui.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
  @Test(groups = {"smoke"})
  public void openBaseUrl() {
    DriverManager.getDriver().get(ConfigManager.get("baseUrl"));
    Assert.assertNotNull(DriverManager.getDriver().getTitle());
  }
}
