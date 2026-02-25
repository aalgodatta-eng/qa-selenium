@ui @uitpg
Feature: Shadow DOM
  Elements inside a Shadow DOM tree are accessed via Selenium 4 getShadowRoot().
  A Generate button creates a GUID inside the shadow DOM input field.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-18 Shadow DOM page loads with shadow host present
    Given I navigate to the shadow dom page
    Then the shadow dom page should be loaded
    And the shadow host element should be present

  @uitpg_regression @positive
  Scenario: UITPG-REG-SDM-01 Shadow host element is present on the page
    Given I navigate to the shadow dom page
    Then the shadow host element should be present

  @uitpg_regression @positive
  Scenario: UITPG-REG-SDM-02 Generate button creates GUID inside Shadow DOM
    Given I navigate to the shadow dom page
    When I click the generate button in shadow DOM
    Then a GUID should be generated in the shadow DOM input

  @uitpg_regression @positive
  Scenario: UITPG-REG-SDM-03 Copy button inside Shadow DOM works
    Given I navigate to the shadow dom page
    When I click the generate button in shadow DOM
    And I click the copy button in shadow DOM
    Then the copy operation in shadow DOM should succeed
