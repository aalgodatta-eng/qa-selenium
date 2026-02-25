@ui @uitpg
Feature: Visibility
  Buttons exist in various visibility states: visible, hidden, transparent,
  off-screen, zero-width, not-displayed, and overlapped.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-25 Visibility page loads
    Given I navigate to the visibility page
    Then the visibility page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-VIS-01 Visible button is displayed on page load
    Given I navigate to the visibility page
    Then the visible button should be displayed on the visibility page

  @uitpg_regression @positive
  Scenario: UITPG-REG-VIS-02 Hidden elements still exist in the DOM after hide
    Given I navigate to the visibility page
    When I click the hide button on the visibility page
    Then hidden elements may still exist in the DOM

  @uitpg_regression @negative
  Scenario: UITPG-REG-VIS-03 Hidden button is not visible after hide action
    Given I navigate to the visibility page
    When I click the hide button on the visibility page
    Then the hidden button should not be visible on the visibility page
