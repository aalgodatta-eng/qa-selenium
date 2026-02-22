package com.algodatta.hooks;

import com.algodatta.core.config.ConfigManager;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Selenoid records videos on the server side.
 * docker-compose-selenoid.yml mounts /opt/selenoid/video -> tests/target/videos.
 * If you pass -DvideoName=some.mp4, Selenoid will create that file and this hook will attach it.
 */
public class VideoHooks {

  @After("@ui")
  public void attachVideoIfPresent(Scenario scenario) {
    boolean enableVideo = ConfigManager.getBoolean("enableVideo", false);
    if (!enableVideo) return;

    String videoName = ConfigManager.get("videoName");
    if (videoName == null || videoName.isBlank()) return;

    Path video = Path.of("target", "videos", videoName);
    if (!Files.exists(video)) return;

    try {
      scenario.attach(Files.readAllBytes(video), "video/mp4", "Execution Video");
    } catch (IOException ignored) {}
  }
}
