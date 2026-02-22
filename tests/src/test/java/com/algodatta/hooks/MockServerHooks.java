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

    String mockPortRaw = System.getProperty("mockPort", "8089");
    if (mockPortRaw == null || mockPortRaw.trim().isEmpty()) {
      mockPortRaw = "8089";
    }
    int port = Integer.parseInt(mockPortRaw.trim());
    server = new WireMockServer(
        WireMockConfiguration.wireMockConfig()
            .port(port)
            .usingFilesUnderClasspath("wiremock")
    );
    server.start();
    System.setProperty("apiBaseUrl", "http://localhost:" + port);
  }

  @After("@mockapi")
  public void stopMock() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }
}
