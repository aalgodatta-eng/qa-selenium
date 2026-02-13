package com.algodatta.core.config;

import com.algodatta.core.constants.FrameworkConstants;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {
  private static final Properties PROPS = new Properties();
  private static volatile boolean loaded = false;
  private ConfigManager() {}

  public static void load() {
    if (loaded) return;
    synchronized (ConfigManager.class) {
      if (loaded) return;
      String env = System.getProperty("env", FrameworkConstants.DEFAULT_ENV);
      String path = FrameworkConstants.ENV_FOLDER + env + ".properties";
      try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
        if (is == null) throw new RuntimeException("Env file not found: " + path);
        PROPS.load(is);
        loaded = true;
      } catch (Exception e) {
        throw new RuntimeException("Failed to load config: " + path, e);
      }
    }
  }

  public static String get(String key) {
    load();
    String sys = System.getProperty(key);
    if (sys != null && !sys.isBlank()) return sys.trim();
    String val = PROPS.getProperty(key);
    return val == null ? null : val.trim();
  }

  public static boolean getBoolean(String key, boolean defaultVal) {
    String v = get(key);
    return (v == null) ? defaultVal : Boolean.parseBoolean(v);
  }

  public static int getInt(String key, int defaultVal) {
    String v = get(key);
    try { return (v == null) ? defaultVal : Integer.parseInt(v); }
    catch (NumberFormatException e) { return defaultVal; }
  }
}
