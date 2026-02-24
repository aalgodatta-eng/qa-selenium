package com.algodatta.api.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * RestAssured {@link Filter} that captures every HTTP exchange as a formatted,
 * human-readable text block.
 *
 * <p>Step definitions retrieve the text via {@link #getLastEntry()} and forward
 * it to {@code scenario.log()} so that both the Extent CucumberAdapter and the
 * Allure Cucumber plugin surface the request / response details inside the
 * relevant scenario node.
 *
 * <p>Response bodies longer than {@value #MAX_BODY_LENGTH} characters are
 * truncated to avoid flooding the reports.
 */
public class ApiLoggingFilter implements Filter {

  private static final int MAX_BODY_LENGTH = 3_000;

  /** Stores the formatted text of the most recent HTTP exchange. */
  private volatile String lastEntry = "";

  @Override
  public Response filter(FilterableRequestSpecification req,
                         FilterableResponseSpecification res,
                         FilterContext ctx) {

    long start    = System.currentTimeMillis();
    Response response = ctx.next(req, res);
    long elapsed  = System.currentTimeMillis() - start;

    StringBuilder sb = new StringBuilder();

    // ── Request ──────────────────────────────────────────────────────────
    sb.append("──── REQUEST ────────────────────────────────────────────\n");
    sb.append(req.getMethod()).append("  ").append(req.getURI()).append("\n");
    req.getHeaders().forEach(h ->
        sb.append("  ").append(h.getName()).append(": ").append(h.getValue()).append("\n"));

    Object rawBody = req.getBody();
    if (rawBody != null) {
      String bodyStr = rawBody instanceof String ? (String) rawBody : "[binary]";
      if (!bodyStr.isBlank()) {
        sb.append("  Body: ").append(truncate(bodyStr)).append("\n");
      }
    }

    // ── Response ─────────────────────────────────────────────────────────
    sb.append("──── RESPONSE (").append(elapsed).append(" ms) ─────────────────────\n");
    sb.append("  Status       : ").append(response.statusCode())
      .append("  ").append(response.statusLine()).append("\n");
    sb.append("  Content-Type : ").append(response.contentType()).append("\n");

    String body = response.asString();
    if (body != null && !body.isBlank()) {
      sb.append("  Body: ").append(truncate(body)).append("\n");
    }

    this.lastEntry = sb.toString();
    return response;
  }

  /**
   * Returns the formatted request + response text for the most recent
   * HTTP call made through this filter instance.
   */
  public String getLastEntry() {
    return lastEntry;
  }

  private static String truncate(String s) {
    return s.length() > MAX_BODY_LENGTH
        ? s.substring(0, MAX_BODY_LENGTH) + "\n  …[truncated — " + s.length() + " chars total]"
        : s;
  }
}
