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
        .build();
  }
}
