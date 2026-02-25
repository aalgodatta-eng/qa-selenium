@ui @uitpg
Feature: Load Delay
  A button appears after a server-side delay. Explicit waits are required
  to interact with the element reliably.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-05 Load Delay page fully loads within timeout
    Given I navigate to the load delay page
    Then the load delay page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-LDL-01 Button appears after page load delay with explicit wait
    Given I navigate to the load delay page
    Then the delayed button should be visible after waiting

  @uitpg_regression @positive
  Scenario: UITPG-REG-LDL-02 Button can be clicked after waiting for it to appear
    Given I navigate to the load delay page
    When I click the delayed button
    Then the load delay page should be fully loaded
