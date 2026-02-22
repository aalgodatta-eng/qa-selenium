package com.algodatta.api.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.Set;

/**
 * Simple exponential backoff retry for flaky network/API responses.
 *
 * Controlled via system properties (all optional):
 * - Dretry=2                      -> total attempts (including first) (default 2)
 * - Dapi.retry.baseDelayMs=250    -> initial delay
 * - Dapi.retry.maxDelayMs=2000    -> cap
 * - Dapi.retry.statusCodes=429,500,502,503,504
 */
public class RetryFilter implements Filter {

  private final int attempts;
  private final long baseDelayMs;
  private final long maxDelayMs;
  private final Set<Integer> retryStatus;

  public RetryFilter() {
    this.attempts = intProp("retry", 2);
    this.baseDelayMs = longProp("api.retry.baseDelayMs", 250);
    this.maxDelayMs = longProp("api.retry.maxDelayMs", 2000);
    this.retryStatus = parseStatusCodes(System.getProperty("api.retry.statusCodes", "429,500,502,503,504"));
  }

  @Override
  public Response filter(
      FilterableRequestSpecification requestSpec,
      FilterableResponseSpecification responseSpec,
      FilterContext ctx) {

    RuntimeException lastException = null;
    Response lastResponse = null;

    for (int i = 1; i <= Math.max(1, attempts); i++) {
      try {
        lastResponse = ctx.next(requestSpec, responseSpec);
        if (lastResponse != null && retryStatus.contains(lastResponse.statusCode()) && i < attempts) {
          sleepBackoff(i);
          continue;
        }
        return lastResponse;
      } catch (RuntimeException ex) {
        lastException = ex;
        if (i >= attempts) {
          throw ex;
        }
        sleepBackoff(i);
      }
    }

    if (lastException != null) {
      throw lastException;
    }
    return lastResponse;
  }

  private void sleepBackoff(int attemptIndex) {
    long delay = Math.min(maxDelayMs, baseDelayMs * (1L << Math.min(10, attemptIndex - 1)));
    // small deterministic jitter based on attempt index
    long jitter = Math.min(150, attemptIndex * 37L);
    try {
      Thread.sleep(delay + jitter);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  private static int intProp(String key, int def) {
    try {
      return Integer.parseInt(System.getProperty(key, String.valueOf(def)).trim());
    } catch (Exception e) {
      return def;
    }
  }

  private static long longProp(String key, long def) {
    try {
      return Long.parseLong(System.getProperty(key, String.valueOf(def)).trim());
    } catch (Exception e) {
      return def;
    }
  }

  private static Set<Integer> parseStatusCodes(String csv) {
    try {
      String[] parts = csv.split(",");
      java.util.HashSet<Integer> set = new java.util.HashSet<>();
      for (String p : parts) {
        String t = p.trim();
        if (!t.isEmpty()) {
          set.add(Integer.parseInt(t));
        }
      }
      return set.isEmpty() ? Set.of(429, 500, 502, 503, 504) : Set.copyOf(set);
    } catch (Exception e) {
      return Set.of(429, 500, 502, 503, 504);
    }
  }
}
