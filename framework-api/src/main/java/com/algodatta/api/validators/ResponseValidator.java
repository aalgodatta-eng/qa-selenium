package com.algodatta.api.validators;

import io.restassured.response.Response;
import org.testng.Assert;

import static io.restassured.path.json.JsonPath.from;

public final class ResponseValidator {
  private ResponseValidator() {}
  public static void status(Response response, int expected) {
    Assert.assertNotNull(response, "Response is null");
    Assert.assertEquals(response.statusCode(), expected, "Unexpected status code");
  }

  public static void headerEquals(Response response, String headerName, String expected) {
    Assert.assertNotNull(response, "Response is null");
    String actual = response.getHeader(headerName);
    Assert.assertNotNull(actual, "Header missing: " + headerName);
    Assert.assertEquals(actual, expected, "Unexpected header value for: " + headerName);
  }

  public static void headerContains(Response response, String headerName, String expectedSubstring) {
    Assert.assertNotNull(response, "Response is null");
    String actual = response.getHeader(headerName);
    Assert.assertNotNull(actual, "Header missing: " + headerName);
    Assert.assertTrue(actual.contains(expectedSubstring),
        "Header '" + headerName + "' did not contain: " + expectedSubstring + " (actual: " + actual + ")");
  }

  public static void contentTypeContains(Response response, String expectedSubstring) {
    Assert.assertNotNull(response, "Response is null");
    String ct = response.getContentType();
    Assert.assertNotNull(ct, "Content-Type is null");
    Assert.assertTrue(ct.toLowerCase().contains(expectedSubstring.toLowerCase()),
        "Content-Type did not contain '" + expectedSubstring + "' (actual: " + ct + ")");
  }

  public static void bodyContains(Response response, String expectedSubstring) {
    Assert.assertNotNull(response, "Response is null");
    String body = response.asString();
    Assert.assertTrue(body.contains(expectedSubstring),
        "Body did not contain: " + expectedSubstring);
  }

  public static void bodyMatchesRegex(Response response, String regex) {
    Assert.assertNotNull(response, "Response is null");
    String body = response.asString();
    Assert.assertTrue(body.matches(regex), "Body did not match regex: " + regex);
  }

  public static void jsonPathEquals(Response response, String jsonPath, Object expected) {
    Assert.assertNotNull(response, "Response is null");
    Object actual = from(response.asString()).get(jsonPath);
    Assert.assertEquals(actual, expected, "Unexpected JSONPath value for: " + jsonPath);
  }

  public static void jsonPathNotNull(Response response, String jsonPath) {
    Assert.assertNotNull(response, "Response is null");
    Object actual = from(response.asString()).get(jsonPath);
    Assert.assertNotNull(actual, "JSONPath was null: " + jsonPath);
  }

  public static void timeLessThanMs(Response response, long maxMs) {
    Assert.assertNotNull(response, "Response is null");
    long time = response.time();
    Assert.assertTrue(time < maxMs, "Response time " + time + "ms exceeded " + maxMs + "ms");
  }
}
