@ui @uitpg
Feature: Mouse Over
  A link requires a hover action before it can be clicked.
  Click counts accumulate across multiple hover-and-click operations.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-15 Mouse Over page loads
    Given I navigate to the mouse over page
    Then the mouse over page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-MSO-01 Hover and click increments the click count
    Given I navigate to the mouse over page
    When I hover and click the mouse-over link
    Then the click count should be at least 1

  @uitpg_regression @positive
  Scenario: UITPG-REG-MSO-02 Multiple hover clicks accumulate the count
    Given I navigate to the mouse over page
    When I hover and click the mouse-over link 3 times
    Then the click count should be exactly 3
