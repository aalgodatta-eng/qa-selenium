package ui.pages;

import com.algodatta.core.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
  private final WebDriver driver;

  public HomePage() {
    this.driver = DriverManager.getDriver();
    PageFactory.initElements(driver, this);
  }

  public String title() {
    return driver.getTitle();
  }
}
