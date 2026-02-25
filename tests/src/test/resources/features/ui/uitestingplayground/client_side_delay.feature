@ui @uitpg
Feature: Client Side Delay
  A result button appears after a JavaScript-driven delay. The test must
  wait for the client-side computation to complete before interacting.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-07 Client Side Delay page loads with generate button visible
    Given I navigate to the client side delay page
    Then the client side delay page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-CSD-01 Result button appears after client-side delay
    Given I navigate to the client side delay page
    When I click the generate button on client side delay page
    Then the result button should appear after the delay

  @uitpg_regression @positive
  Scenario: UITPG-REG-CSD-02 Complete client-side delay flow executes successfully
    Given I navigate to the client side delay page
    When I perform the complete client side delay flow
    Then the complete client side delay flow should succeed

  @uitpg_regression @negative
  Scenario: UITPG-REG-CSD-03 Result button not present before triggering the delay
    Given I navigate to the client side delay page
    Then the result button should not be present before clicking generate
