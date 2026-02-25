@ui @uitpg
Feature: Sample App
  A login form with dynamically-generated element IDs. Stable name attributes
  must be used instead of IDs to locate elements reliably.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-14 Sample App page loads with login form
    Given I navigate to the sample app page
    Then the sample app page should be loaded
    And the sample app login button should be visible

  @uitpg_regression @positive
  Scenario: UITPG-REG-SMP-01 Login with valid credentials succeeds
    Given I navigate to the sample app page
    When I login to the sample app with username "testuser" and password "pwd"
    Then I should be logged in to the sample app

  @uitpg_regression @negative
  Scenario: UITPG-REG-SMP-02 Login with invalid password shows error
    Given I navigate to the sample app page
    When I login to the sample app with username "testuser" and password "wrongpassword"
    Then the sample app login should fail

  @uitpg_regression @negative
  Scenario: UITPG-REG-SMP-03 Login with empty username is rejected
    Given I navigate to the sample app page
    When I login to the sample app with username "" and password "pwd"
    Then I should not be logged in to the sample app

  @uitpg_regression @positive
  Scenario: UITPG-REG-SMP-04 Page elements are accessible using stable name attributes
    Given I navigate to the sample app page
    Then the sample app page should be loaded
