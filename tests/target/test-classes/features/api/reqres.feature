@api_smoke
Feature: ReqRes API validation

  # ReqRes is a separate API at https://reqres.in — it is NOT httpbin.
  # We explicitly set the base URL for this feature instead of using the
  # env-default apiBaseUrl (which points to httpbin.org for the qa environment).

  Scenario: Login then fetch user details
    Given I use base url "https://reqres.in"
    When I login with email "eve.holt@reqres.in" and password "cityslicka"
    Then the response status should be 200
    And json path "token" should not be null
    When I GET "/api/users/2"
    Then the response status should be 200
    And the response should match json schema "schemas/reqres_user_schema.json"
