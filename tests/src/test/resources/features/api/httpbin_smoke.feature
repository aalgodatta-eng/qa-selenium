@api_smoke
Feature: httpbin Smoke API suite

  Background:
    Given I set API base url
    And I reset API client

  Scenario: Basic GET should echo request data
    When I send "GET" request to "/get?hello=world"
    Then the response status should be 200
    And the response content type should contain "application/json"
    And the JSON path "args.hello" should be "world"
    And the JSON path "method" should be "GET"

  Scenario: Basic POST should echo JSON payload
    Given I set header "Accept" to "application/json"
    And I set JSON body
      """
      {"name":"kc","type":"smoke"}
      """
    When I send "POST" request to "/post"
    Then the response status should be 200
    And the response content type should contain "application/json"
    And the JSON path "json.name" should be "kc"
    And the JSON path "json.type" should be "smoke"

  Scenario: Basic Auth positive
    Given I use basic auth "user" and "passwd"
    When I send "GET" request to "/basic-auth/user/passwd"
    Then the response status should be 200
    And the JSON path "authenticated" should not be null

  Scenario: Basic Auth negative
    Given I use basic auth "user" and "wrong"
    When I send "GET" request to "/basic-auth/user/passwd"
    Then the response status should be 401
