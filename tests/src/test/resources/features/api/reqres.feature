@api_smoke
Feature: ReqRes API validation

  Scenario: Login then fetch user details
    Given I set API base url
    When I login with email "eve.holt@reqres.in" and password "cityslicka"
    Then the response status should be 200
    When I GET "/api/users/2"
    Then the response status should be 200
    And the response should match json schema "schemas/reqres_user_schema.json"
