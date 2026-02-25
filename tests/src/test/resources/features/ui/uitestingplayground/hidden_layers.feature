@ui @uitpg
Feature: Hidden Layers
  An overlapping blue button appears after clicking the green button,
  preventing further clicks on the green button underneath.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-04 Hidden Layers page loads with green button visible
    Given I navigate to the hidden layers page
    Then the hidden layers page should be loaded
    And the green button should be visible

  @uitpg_regression @positive
  Scenario: UITPG-REG-HID-01 Click green button before blue overlay appears
    Given I navigate to the hidden layers page
    Then the green button should be visible
    When I click the green button
    Then the blue button should appear on top

  @uitpg_regression @negative
  Scenario: UITPG-REG-HID-02 Green button not clickable after blue button overlaps it
    Given I navigate to the hidden layers page
    When I click the green button
    Then the blue button should appear on top
    And the green button should not be directly clickable
