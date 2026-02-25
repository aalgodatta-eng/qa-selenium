@ui @uitpg
Feature: Auto Wait
  Demonstrates automatic waiting for elements to become interactable.
  The apply button triggers a target element that needs waiting before interaction.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-23 Auto Wait page loads
    Given I navigate to the auto wait page
    Then the auto wait page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-AWA-01 Click apply and wait for target element to become interactable
    Given I navigate to the auto wait page
    When I click the apply button on the auto wait page
    Then the auto wait page should still be loaded after applying
