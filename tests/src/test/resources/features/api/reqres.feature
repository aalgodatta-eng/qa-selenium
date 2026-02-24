@api_smoke
Feature: ReqRes API validation

  # ReqRes moved POST endpoints behind a paywall; the scenario runs against
  # WireMock so it stays fast and reliable without external dependencies.

  @mockapi
  Scenario: Login then fetch user details
    Given I set API base url
    When I login with email "eve.holt@reqres.in" and password "cityslicka"
    Then the response status should be 200
    And json path "token" should not be null
    When I GET "/api/users/2"
    Then the response status should be 200
    And the response should match json schema "schemas/reqres_user_schema.json"
