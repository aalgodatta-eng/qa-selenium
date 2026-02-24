package com.algodatta.context;

import io.cucumber.java.Scenario;

/**
 * Shared scenario state for Cucumber PicoContainer injection.
 *
 * <p>PicoContainer creates <em>one instance per scenario</em> and injects the
 * same object into every step-definition class and hook class that declares it
 * as a constructor parameter.  This lets {@link com.algodatta.hooks.Hooks}
 * register the live {@link Scenario} reference in {@code @Before(order = 0)},
 * after which any step definition (e.g. {@code ApiSteps}) can call
 * {@link #log(String)} or {@link #attach(byte[], String, String)} to add rich
 * content that is surfaced by both the Extent CucumberAdapter and the Allure
 * Cucumber plugin.
 *
 * <p>All methods are null-safe — if {@code setScenario} has not been called
 * yet (e.g. in unit tests) the log / attach calls are silently ignored.
 */
public class ScenarioContext {

  private Scenario scenario;

  // ── Registration ─────────────────────────────────────────────────────────

  /**
   * Called by {@code Hooks.registerScenario()} in {@code @Before(order = 0)}
   * to bind the live Cucumber {@link Scenario} before any step runs.
   */
  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  /** @return the currently active {@link Scenario}, or {@code null} if unset. */
  public Scenario get() {
    return scenario;
  }

  // ── Reporting helpers ─────────────────────────────────────────────────────

  /**
   * Appends a plain-text log line to the Cucumber scenario output.
   * Both the Extent CucumberAdapter and AllureCucumber7Jvm surface this
   * inside the current scenario / step node.
   */
  public void log(String message) {
    if (scenario != null) {
      scenario.log(message);
    }
  }

  /**
   * Attaches binary content (screenshot, JSON, etc.) to the Cucumber scenario.
   * Both reporters treat {@code scenario.attach()} as a first-class attachment.
   *
   * @param bytes     raw bytes of the attachment
   * @param mediaType MIME type, e.g. {@code "image/png"} or {@code "application/json"}
   * @param name      display name shown in the report
   */
  public void attach(byte[] bytes, String mediaType, String name) {
    if (scenario != null) {
      scenario.attach(bytes, mediaType, name);
    }
  }

  /** @return {@code true} when the active scenario has at least one failed step. */
  public boolean isFailed() {
    return scenario != null && scenario.isFailed();
  }
}
