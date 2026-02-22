package com.algodatta.api.specs;

import com.algodatta.core.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class SpecFactory {
  private SpecFactory() {}
  public static RequestSpecification baseSpec() {
    return new RequestSpecBuilder()
        .setBaseUri(ConfigManager.get("apiBaseUrl"))
        .setContentType(ContentType.JSON)
        .addHeader("Accept", "application/json")
        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36")
        .build();
  }
}
