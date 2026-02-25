@ui @uitpg
Feature: Alerts
  JavaScript alert, confirm, and prompt dialogs must be handled using
  WebDriver's Alert API (accept/dismiss/sendKeys).

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-19 Alerts page loads with trigger buttons visible
    Given I navigate to the alerts page
    Then the alerts page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-ALT-01 Accept JavaScript alert
    Given I navigate to the alerts page
    When I trigger and accept the JavaScript alert
    Then the JavaScript alert should be accepted without error

  @uitpg_regression @positive
  Scenario: UITPG-REG-ALT-02 Accept confirm dialog and verify result
    Given I navigate to the alerts page
    When I trigger and accept the confirm dialog
    Then the confirm result should indicate OK was pressed

  @uitpg_regression @negative
  Scenario: UITPG-REG-ALT-03 Dismiss confirm dialog and verify cancel result
    Given I navigate to the alerts page
    When I trigger and dismiss the confirm dialog
    Then the confirm result should indicate Cancel was pressed

  @uitpg_regression @positive
  Scenario: UITPG-REG-ALT-04 Accept prompt dialog with text input
    Given I navigate to the alerts page
    When I trigger the prompt and enter text "AutomationInput"
    Then the prompt result should contain "AutomationInput"

  @uitpg_regression @negative
  Scenario: UITPG-REG-ALT-05 Dismiss prompt without entering text
    Given I navigate to the alerts page
    When I trigger and dismiss the prompt
    Then the prompt should be dismissed without error
