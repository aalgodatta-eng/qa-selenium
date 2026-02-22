@api @mockapi @demo
Feature: Dummy API app validation using WireMock

  Scenario: Login then fetch a product
    Given I set API base url
    When I login with email "kc" and password "sdet123"
    Then the response status should be 200
    When I GET "/api/products/1"
    Then the response status should be 200
