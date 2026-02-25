@ui @uitpg
Feature: Progress Bar
  A progress bar fills from 0% to 100%. Tests verify starting state,
  stopping near a target value, and that stopped progress does not continue.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-13 Progress Bar page loads with start button
    Given I navigate to the progress bar page
    Then the progress bar page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-PRG-01 Progress bar starts at 0 percent
    Given I navigate to the progress bar page
    Then the progress should start at 0%

  @uitpg_regression @positive
  Scenario: UITPG-REG-PRG-02 Stop progress bar near target percentage
    Given I navigate to the progress bar page
    When I start the progress bar
    And I wait for progress to reach 75% then stop
    Then the progress should be near 75% with 10% tolerance

  @uitpg_regression @positive
  Scenario: UITPG-REG-PRG-03 Progress bar increases after start is clicked
    Given I navigate to the progress bar page
    When I start the progress bar
    And I wait for 1000 milliseconds
    Then the progress should be greater than 0%

  @uitpg_regression @negative
  Scenario: UITPG-REG-PRG-04 Progress stays stopped after stop button is clicked
    Given I navigate to the progress bar page
    When I start the progress bar
    And I wait for 500 milliseconds
    And I stop the progress bar
    Then the progress should remain unchanged after stopping
