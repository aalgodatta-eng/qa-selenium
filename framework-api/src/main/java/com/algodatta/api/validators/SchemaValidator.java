package com.algodatta.api.validators;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public final class SchemaValidator {
  private SchemaValidator() {}
  public static void validate(Response response, String classpathSchema) {
    response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(classpathSchema));
  }
}
