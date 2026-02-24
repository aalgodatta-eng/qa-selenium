package com.algodatta.hooks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class MockServerHooks {

  private static WireMockServer server;

  @Before("@mockapi")
  public void startMock() {
    if (server != null && server.isRunning()) return;

    String mockPortStr = System.getProperty("mockPort", "").trim();
    int port = mockPortStr.isEmpty() ? 8089 : Integer.parseInt(mockPortStr);
    server = new WireMockServer(
        WireMockConfiguration.wireMockConfig()
            .port(port)
            .usingFilesUnderClasspath("wiremock")
    );
    server.start();

    // Override apiBaseUrl so subsequent "I set API base url" steps
    // correctly point to the running mock server.
    System.setProperty("apiBaseUrl", "http://localhost:" + port);
  }

  @After("@mockapi")
  public void stopMock() {
    if (server != null) {
      server.stop();
      server = null;
    }
    // Clear the system property so subsequent non-mockapi scenarios
    // that call "I set API base url" use the env config value, not the
    // now-dead mock URL. Without this, any test after @mockapi would
    // point at http://localhost:8089 which is no longer running.
    System.clearProperty("apiBaseUrl");
  }
}
