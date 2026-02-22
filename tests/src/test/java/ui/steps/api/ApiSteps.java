package ui.steps.api;

import com.algodatta.api.specs.SpecFactory;
import com.algodatta.api.validators.SchemaValidator;
import com.algodatta.api.validators.ResponseValidator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiSteps {

  private Response last;
  private RequestSpecification req;
  private final SessionFilter session = new SessionFilter();
  private boolean followRedirects = true;

  // ── Base URL ──────────────────────────────────────────────────────────────

  /**
   * Sets the base URL from the environment config (qa.properties → apiBaseUrl).
   * Also resets the request client so it uses the new spec.
   */
  @Given("I set API base url")
  public void setBaseUrl() {
    RestAssured.requestSpecification = SpecFactory.baseSpec();
    resetClient();
  }

  /**
   * Sets an explicit base URL instead of reading from config.
   * Use this when a scenario needs a different host than the env default
   * (e.g. reqres.in vs httpbin.org).
   */
  @Given("I use base url {string}")
  public void useBaseUrl(String url) {
    RestAssured.requestSpecification = new RequestSpecBuilder()
        .setBaseUri(url)
        .setContentType(ContentType.JSON)
        .addHeader("Accept", "application/json")
        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        .build();
    resetClient();
  }

  // ── Client reset & headers ────────────────────────────────────────────────

  @Given("I reset API client")
  public void resetClient() {
    this.req = RestAssured.given()
        .spec(RestAssured.requestSpecification)
        .filter(session);
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
    this.req = this.req.contentType(ContentType.JSON).body(json);
  }

  // ── Auth ──────────────────────────────────────────────────────────────────

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

  // ── Redirect control ──────────────────────────────────────────────────────

  @Given("I follow redirects")
  public void followRedirects() {
    this.followRedirects = true;
  }

  @Given("I do not follow redirects")
  public void noFollowRedirects() {
    this.followRedirects = false;
  }

  // ── HTTP methods ──────────────────────────────────────────────────────────

  @When("I send {string} request to {string}")
  public void send(String method, String path) {
    RequestSpecification r = this.req.redirects().follow(this.followRedirects);
    switch (method.toUpperCase()) {
      case "GET"     -> last = r.get(path);
      case "POST"    -> last = r.post(path);
      case "PUT"     -> last = r.put(path);
      case "PATCH"   -> last = r.patch(path);
      case "DELETE"  -> last = r.delete(path);
      case "HEAD"    -> last = r.head(path);
      case "OPTIONS" -> last = r.options(path);
      default        -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }
  }

  /** Shorthand: GET without needing full "I send ... request to ..." syntax. */
  @When("I GET {string}")
  public void get(String path) {
    send("GET", path);
  }

  /** Shorthand: DELETE. */
  @When("I DELETE {string}")
  public void delete(String path) {
    send("DELETE", path);
  }

  /**
   * POST with an inline docstring JSON body.
   * Usage in feature:
   *   When I POST "/posts" with json body:
   *     """
   *     { "title": "foo", "userId": 1 }
   *     """
   */
  @When("I POST {string} with json body:")
  public void postWithBody(String path, String body) {
    last = this.req
        .contentType(ContentType.JSON)
        .body(body)
        .redirects().follow(this.followRedirects)
        .post(path);
  }

  /** PUT with an inline docstring JSON body. */
  @When("I PUT {string} with json body:")
  public void putWithBody(String path, String body) {
    last = this.req
        .contentType(ContentType.JSON)
        .body(body)
        .redirects().follow(this.followRedirects)
        .put(path);
  }

  /** PATCH with an inline docstring JSON body. */
  @When("I PATCH {string} with json body:")
  public void patchWithBody(String path, String body) {
    last = this.req
        .contentType(ContentType.JSON)
        .body(body)
        .redirects().follow(this.followRedirects)
        .patch(path);
  }

  /**
   * Generic login step — POSTs JSON credentials to /api/login.
   * Works for:
   *   - reqres.in  : POST https://reqres.in/api/login
   *   - WireMock   : POST http://localhost:8089/api/login (stub mapped in login.json)
   */
  @When("I login with email {string} and password {string}")
  public void loginWithEmail(String email, String password) {
    last = this.req
        .contentType(ContentType.JSON)
        .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
        .redirects().follow(this.followRedirects)
        .post("/api/login");
  }

  // ── Assertions — full "the response ..." phrasing ─────────────────────────

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
  public void jsonPathContainsString(String path, String expectedSubstring) {
    ResponseValidator.jsonPathNotNull(last, path);
    String actualStr = String.valueOf(JsonPath.from(last.asString()).get(path));
    Assert.assertTrue(actualStr.contains(expectedSubstring),
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

  // ── Assertions — shorthand "json path ..." (no leading "the response") ────
  // These mirror the full phrasing above but match the step text used in
  // jsonplaceholder_smoke.feature and other feature files.

  @Then("json path {string} should be {int}")
  public void jsonPathShortInt(String path, Integer expected) {
    ResponseValidator.jsonPathEquals(last, path, expected);
  }

  @Then("json path {string} should be {string}")
  public void jsonPathShortString(String path, String expected) {
    ResponseValidator.jsonPathEquals(last, path, expected);
  }

  @Then("json path {string} should not be null")
  public void jsonPathShortNotNull(String path) {
    ResponseValidator.jsonPathNotNull(last, path);
  }

  @Then("json path {string} should contain {string}")
  public void jsonPathShortContains(String path, String expectedSubstring) {
    Object actual = JsonPath.from(last.asString()).get(path);
    Assert.assertNotNull(actual, "JSONPath '" + path + "' was null");
    String actualStr = String.valueOf(actual);
    Assert.assertTrue(actualStr.contains(expectedSubstring),
        "Expected JSONPath '" + path + "' to contain '" + expectedSubstring + "' but was: " + actualStr);
  }

  // ── Array assertions ──────────────────────────────────────────────────────

  /**
   * Asserts that a JSON array at the given JsonPath is larger than minSize.
   * Use "$" for a root-level array response.
   * Example: the response array at "$" size should be greater than 0
   */
  @Then("the response array at {string} size should be greater than {int}")
  public void arraySizeGreaterThan(String path, int minSize) {
    List<?> list = JsonPath.from(last.asString()).getList(path);
    Assert.assertNotNull(list, "Array at path '" + path + "' was null");
    Assert.assertTrue(list.size() > minSize,
        "Expected array size > " + minSize + " but was " + list.size());
  }

  /**
   * Asserts every item in the root array response has a given field equal to an int.
   * Uses RestAssured's list extraction: getList("fieldName") over a root array.
   * Example: each item in response array has json path "userId" equals 1
   */
  @Then("each item in response array has json path {string} equals {int}")
  public void eachItemJsonPathEqualsInt(String field, int expected) {
    List<Integer> values = JsonPath.from(last.asString()).getList(field);
    Assert.assertNotNull(values, "Field '" + field + "' not found in response array");
    Assert.assertFalse(values.isEmpty(), "Response array was empty");
    for (int i = 0; i < values.size(); i++) {
      Assert.assertEquals((int) values.get(i), expected,
          "Item [" + i + "] field '" + field + "' expected " + expected + " but was " + values.get(i));
    }
  }
}
