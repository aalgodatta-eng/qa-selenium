@ui @uitpg
Feature: Dynamic ID
  Elements with dynamically generated IDs must be located using stable
  selectors such as class names instead of the dynamic ID attribute.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-02 Dynamic ID page loads with button visible
    Given I navigate to the dynamic id page
    Then the dynamic id page should be loaded
    And the dynamic button should be displayed

  @uitpg_regression @positive
  Scenario: UITPG-REG-DYN-01 Click dynamic button using stable class selector
    Given I navigate to the dynamic id page
    Then the dynamic button should be displayed
    When I click the dynamic button
    Then the dynamic button should still be displayed after click

  @uitpg_regression @positive
  Scenario: UITPG-REG-DYN-02 Button ID changes on each page reload
    Given I navigate to the dynamic id page
    When I record the current dynamic button ID
    And I reload the dynamic id page
    Then the button ID should be different from the recorded one

  @uitpg_regression @negative
  Scenario: UITPG-REG-DYN-03 Button is still findable via stable selector despite dynamic ID
    Given I navigate to the dynamic id page
    When I record the current dynamic button ID
    And I reload the dynamic id page
    Then the button ID should be different from the recorded one
    And the button should still be findable via stable class selector
