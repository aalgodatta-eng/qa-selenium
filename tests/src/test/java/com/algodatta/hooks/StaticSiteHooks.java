package com.algodatta.hooks;

import com.algodatta.core.config.ConfigManager;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Starts a lightweight static server for the bundled sample-apps so UI scenarios
 * can run without requiring a separate "npm serve" or external process.
 *
 * Serves: http://localhost:8000/sample-apps/** -> <repoRoot>/sample-apps/**
 */
public class StaticSiteHooks {
  private static HttpServer server;

  @BeforeAll(order = 0)
  public static void startStaticServer() {
    String url = ConfigManager.get("dummyUiUrl");
    if (url == null) return;
    if (!url.contains("localhost:8000")) return;

    try {
      if (server != null) return;

      // During Maven test phase, user.dir points to "<repo>/tests". sample-apps is at "<repo>/sample-apps"
      Path repoRoot = Path.of(System.getProperty("user.dir")).normalize().getParent();
      if (repoRoot == null) return;
      Path sampleAppsDir = repoRoot.resolve("sample-apps").normalize();
      if (!Files.exists(sampleAppsDir)) return;

      server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
      server.createContext("/sample-apps", ex -> handleStatic(ex, sampleAppsDir));
      server.setExecutor(null);
      server.start();
    } catch (Exception e) {
      throw new RuntimeException("Failed to start local static server on port 8000", e);
    }
  }

  private static void handleStatic(HttpExchange exchange, Path sampleAppsDir) throws IOException {
    String reqPath = exchange.getRequestURI().getPath(); // /sample-apps/ui/index.html
    String rel = reqPath.replaceFirst("^/sample-apps/?", "");
    if (rel.isEmpty()) rel = "ui/index.html";
    Path file = sampleAppsDir.resolve(rel).normalize();

    if (!file.startsWith(sampleAppsDir) || !Files.exists(file) || Files.isDirectory(file)) {
      exchange.sendResponseHeaders(404, -1);
      exchange.close();
      return;
    }

    byte[] bytes = Files.readAllBytes(file);
    Headers h = exchange.getResponseHeaders();
    h.add("Content-Type", guessContentType(file));
    exchange.sendResponseHeaders(200, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  private static String guessContentType(Path file) {
    String name = file.getFileName().toString().toLowerCase();
    if (name.endsWith(".html")) return "text/html; charset=utf-8";
    if (name.endsWith(".css")) return "text/css; charset=utf-8";
    if (name.endsWith(".js")) return "application/javascript; charset=utf-8";
    if (name.endsWith(".json")) return "application/json; charset=utf-8";
    if (name.endsWith(".png")) return "image/png";
    if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
    if (name.endsWith(".svg")) return "image/svg+xml";
    return "application/octet-stream";
  }

  @AfterAll
  public static void stopStaticServer() {
    if (server != null) {
      server.stop(0);
      server = null;
    }
  }
}
