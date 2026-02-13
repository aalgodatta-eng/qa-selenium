package com.algodatta.core.constants;

public final class FrameworkConstants {
  private FrameworkConstants() {}
  public static final String DEFAULT_ENV = "qa";
  public static final String ENV_FOLDER = "env/";
  public static final String EXTENT_OUT_DIR = "tests/target/extent-report/";
  public static final String EXTENT_HTML = EXTENT_OUT_DIR + "index.html";
  public static final int DEFAULT_TIMEOUT_SEC = 20;
}
