@ui @uitpg
Feature: Disabled Input
  An input field is initially disabled. Clicking an Enable button makes it
  editable. Typing in the disabled state should be rejected.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-22 Disabled Input page loads with enable button
    Given I navigate to the disabled input page
    Then the disabled input page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-DIN-01 Input becomes enabled after clicking enable button
    Given I navigate to the disabled input page
    Then the input field should be disabled initially
    When I click the enable button and wait for the input
    Then the input field should become enabled

  @uitpg_regression @positive
  Scenario: UITPG-REG-DIN-02 Can type in input after it becomes enabled
    Given I navigate to the disabled input page
    When I click the enable button and wait for the input
    And I type "Automation Text" into the now-enabled input field
    Then the enabled input field should contain "Automation Text"

  @uitpg_regression @negative
  Scenario: UITPG-REG-DIN-03 Cannot type in disabled input field
    Given I navigate to the disabled input page
    Then the input field should be disabled initially
    And typing in the disabled input field should fail
