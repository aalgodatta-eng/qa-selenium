package ui.steps.api;

import com.algodatta.api.specs.SpecFactory;
import com.algodatta.api.validators.SchemaValidator;
import com.algodatta.api.validators.ResponseValidator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class ApiSteps {
  private Response last;
  private RequestSpecification req;
  private final SessionFilter session = new SessionFilter();
  private boolean followRedirects = true;

  @Given("I set API base url")
  public void setBaseUrl() {
    RestAssured.requestSpecification = SpecFactory.baseSpec();
    resetClient();
  }

  @Given("I reset API client")
  public void resetClient() {
    // Start fresh request builder but keep a session filter for cookie-based flows.
    this.req = RestAssured.given().spec(RestAssured.requestSpecification).filter(session);
    this.followRedirects = true;
  }

  @Given("I set headers")
  public void setHeaders(DataTable table) {
    Map<String, String> headers = new HashMap<>();
    for (Map<String, String> row : table.asMaps()) {
      headers.put(row.get("name"), row.get("value"));
    }
    this.req = this.req.headers(headers);
  }

  @Given("I set header {string} to {string}")
  public void setHeader(String name, String value) {
    this.req = this.req.header(name, value);
  }

  @Given("I set query param {string} to {string}")
  public void setQueryParam(String name, String value) {
    this.req = this.req.queryParam(name, value);
  }

  @Given("I set form params")
  public void setFormParams(DataTable table) {
    Map<String, String> form = new HashMap<>();
    for (Map<String, String> row : table.asMaps()) {
      form.put(row.get("name"), row.get("value"));
    }
    this.req = this.req.formParams(form);
  }

  @Given("I set JSON body")
  public void setJsonBody(String json) {
    this.req = this.req.body(json);
  }

  @Given("I use basic auth {string} and {string}")
  public void basicAuth(String user, String pass) {
    this.req = this.req.auth().preemptive().basic(user, pass);
  }

  @Given("I use digest auth {string} and {string}")
  public void digestAuth(String user, String pass) {
    this.req = this.req.auth().digest(user, pass);
  }

  @Given("I use bearer token {string}")
  public void bearer(String token) {
    this.req = this.req.auth().oauth2(token);
  }

  @Given("I follow redirects")
  public void followRedirects() {
    this.followRedirects = true;
  }

  @Given("I do not follow redirects")
  public void noFollowRedirects() {
    this.followRedirects = false;
  }

  @When("I send {string} request to {string}")
  public void send(String method, String path) {
    RequestSpecification r = this.req.redirects().follow(this.followRedirects);
    switch (method.toUpperCase()) {
      case "GET" -> last = r.get(path);
      case "POST" -> last = r.post(path);
      case "PUT" -> last = r.put(path);
      case "PATCH" -> last = r.patch(path);
      case "DELETE" -> last = r.delete(path);
      case "HEAD" -> last = r.head(path);
      case "OPTIONS" -> last = r.options(path);
      default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }
  }

  @When("I GET {string}")
  public void get(String path) {
    send("GET", path);
  }

  @Then("the response status should be {int}")
  public void status(Integer code) {
    ResponseValidator.status(last, code);
  }

  @Then("the response content type should contain {string}")
  public void contentTypeContains(String expected) {
    ResponseValidator.contentTypeContains(last, expected);
  }

  @Then("the response header {string} should contain {string}")
  public void headerContains(String name, String expected) {
    ResponseValidator.headerContains(last, name, expected);
  }

  @Then("the response header {string} should be {string}")
  public void headerEquals(String name, String expected) {
    ResponseValidator.headerEquals(last, name, expected);
  }

  @Then("the response body should contain {string}")
  public void bodyContains(String expected) {
    ResponseValidator.bodyContains(last, expected);
  }

  @Then("the response body should match regex {string}")
  public void bodyRegex(String regex) {
    ResponseValidator.bodyMatchesRegex(last, regex);
  }

  @Then("the JSON path {string} should be {string}")
  public void jsonPathEqualsString(String path, String expected) {
    ResponseValidator.jsonPathEquals(last, path, expected);
  }

  @Then("the JSON path {string} should contain {string}")
  public void jsonPathContains(String path, String expectedSubstring) {
    ResponseValidator.jsonPathNotNull(last, path);
    Object actual = io.restassured.path.json.JsonPath.from(last.asString()).get(path);
    if (actual == null) {
      throw new AssertionError("JSONPath was null: " + path);
    }
    String actualStr = String.valueOf(actual);
    org.testng.Assert.assertTrue(actualStr.contains(expectedSubstring),
        "Expected JSONPath '" + path + "' to contain '" + expectedSubstring + "' but was: " + actualStr);
  }

  @Then("the JSON path {string} should be {int}")
  public void jsonPathEqualsInt(String path, Integer expected) {
    ResponseValidator.jsonPathEquals(last, path, expected);
  }

  @Then("the JSON path {string} should not be null")
  public void jsonPathNotNull(String path) {
    ResponseValidator.jsonPathNotNull(last, path);
  }

  @Then("the response time should be less than {int} ms")
  public void responseTimeLessThan(Integer ms) {
    ResponseValidator.timeLessThanMs(last, ms);
  }

  @Then("the response should match json schema {string}")
  public void schema(String schema) {
    SchemaValidator.validate(last, schema);
  }
}
