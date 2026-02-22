package ui.pages;

import com.algodatta.core.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DummyLoginPage {
  private final WebDriver driver;

  @FindBy(id = "username") private WebElement username;
  @FindBy(id = "password") private WebElement password;
  @FindBy(id = "loginBtn") private WebElement loginBtn;
  @FindBy(id = "msg") private WebElement msg;

  public DummyLoginPage() {
    this.driver = DriverManager.getDriver();
    PageFactory.initElements(driver, this);
  }

  public void open(String url) { driver.get(url); }

  public void login(String u, String p) {
    username.clear(); username.sendKeys(u);
    password.clear(); password.sendKeys(p);
    loginBtn.click();
  }

  public String message() { return msg.getText(); }
}
