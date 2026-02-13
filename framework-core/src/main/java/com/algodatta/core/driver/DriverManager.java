package com.algodatta.core.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {
  private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();
  private DriverManager() {}
  static void setDriver(WebDriver driver) { TL.set(driver); }
  public static WebDriver getDriver() { return TL.get(); }
  public static void quit() {
    WebDriver d = TL.get();
    if (d != null) {
      d.quit();
      TL.remove();
    }
  }
}
