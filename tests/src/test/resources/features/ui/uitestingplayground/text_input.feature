@ui @uitpg
Feature: Text Input
  Typing text into an input field updates the label on a button.
  Covers standard sendKeys, Actions typing, value retention, and edge cases.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-09 Text Input page loads with input field visible
    Given I navigate to the text input page
    Then the text input page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-TXT-01 Typing in field updates button text correctly
    Given I navigate to the text input page
    When I type "Automation Test" into the button name field
    And I click the change button
    Then the button text should be updated to "Automation Test"

  @uitpg_regression @positive
  Scenario: UITPG-REG-TXT-02 Actions class typing also updates button text
    Given I navigate to the text input page
    When I type "KC Automation" into the button name field via Actions
    And I click the change button
    Then the button text should be updated to "KC Automation"

  @uitpg_regression @positive
  Scenario: UITPG-REG-TXT-03 Input field retains typed value before submit
    Given I navigate to the text input page
    When I type "VerifyValue" into the button name field
    Then the input field should retain value "VerifyValue"

  @uitpg_regression @negative
  Scenario: UITPG-REG-TXT-04 Empty input results in empty or default button text
    Given I navigate to the text input page
    When I clear the text input field
    And I click the change button
    Then the button text should not be null after empty input

  @uitpg_regression @positive
  Scenario: UITPG-REG-TXT-05 Special characters in input field work correctly
    Given I navigate to the text input page
    When I type "Test@123!" into the button name field
    And I click the change button
    Then the button text should be updated to "Test@123!"
