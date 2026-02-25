@ui @uitpg
Feature: Click
  Demonstrates the difference between standard Selenium click, Actions click,
  and JavaScript click with respect to triggering DOM event listeners.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-08 Click page loads with button visible
    Given I navigate to the click page
    Then the click page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-CLK-01 Standard Selenium click fires DOM event listeners
    Given I navigate to the click page
    When I click the click-page button using standard Selenium click
    Then the click-page button should turn green indicating event was fired

  @uitpg_regression @positive
  Scenario: UITPG-REG-CLK-02 Actions click also fires DOM event listeners
    Given I navigate to the click page
    When I click the click-page button using Actions click
    Then the click-page button should turn green indicating event was fired

  @uitpg_regression @negative
  Scenario: UITPG-REG-CLK-03 JavaScript click may not fire DOM event listeners
    Given I navigate to the click page
    When I click the click-page button using JavaScript click
    Then the JavaScript click behavior on click page should be documented
