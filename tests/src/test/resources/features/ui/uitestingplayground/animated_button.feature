@ui @uitpg
Feature: Animated Button
  A button animates using CSS. Clicking during animation may fail;
  the test waits for the animation to stop before clicking.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-21 Animated Button page loads with button visible
    Given I navigate to the animated button page
    Then the animated button page should be loaded
    And the animated button should be visible

  @uitpg_regression @positive
  Scenario: UITPG-REG-ANM-01 Click button after animation stops
    Given I navigate to the animated button page
    When I wait for the animation to stop and click the button
    Then the animated button should be clicked after animation stops

  @uitpg_regression @positive
  Scenario: UITPG-REG-ANM-02 Button is initially in animation state on page load
    Given I navigate to the animated button page
    Then the animated button should be in animation state on page load

  @uitpg_regression @negative
  Scenario: UITPG-REG-ANM-03 Button initial animation class is present and non-empty
    Given I navigate to the animated button page
    Then the animated button initial class should not be empty
    When I wait for the animation to stop and click the button
    Then the animated button should be clicked after animation stops
