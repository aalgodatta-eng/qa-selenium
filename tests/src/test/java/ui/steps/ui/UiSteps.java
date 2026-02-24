
package ui.steps.ui;

import com.algodatta.core.config.ConfigManager;
import com.algodatta.core.driver.DriverManager;
import ui.pages.HomePage;
import ui.pages.DummyLoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class UiSteps {
  private HomePage home;
  private DummyLoginPage dummy;

  @Given("I open the application")
  public void openApp() {
    DriverManager.getDriver().get(ConfigManager.get("baseUrl"));
    home = new HomePage();
  }

  @Then("the page title should not be empty")
  public void titleNotEmpty() {
    Assert.assertNotNull(home.title());
    Assert.assertFalse(home.title().trim().isEmpty(), "Title should not be empty");
  }

  @Given("I open the dummy ui app")
  public void openDummyUiApp() {
    // Serve sample app locally: python -m http.server 8000 (from repo root)
    String url = System.getProperty("dummyUiUrl", "http://localhost:8000/sample-apps/ui/index.html");
    dummy = new DummyLoginPage();
    dummy.open(url);
  }

  @When("I login with username {string} and password {string}")
  public void loginDummy(String u, String p) {
    dummy.login(u, p);
  }

  @Then("I should see message {string}")
  public void verifyMessage(String expected) {
    Assert.assertEquals(dummy.message().trim(), expected);
  }

}
