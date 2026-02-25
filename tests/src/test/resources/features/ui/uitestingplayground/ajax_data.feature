@ui @uitpg
Feature: AJAX Data
  Content loads asynchronously after clicking a button. Explicit waits
  are required to detect when the AJAX response has been rendered.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-06 AJAX Data page loads and trigger button is clickable
    Given I navigate to the ajax data page
    Then the ajax data page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-AJX-01 AJAX content loads after clicking trigger button
    Given I navigate to the ajax data page
    When I click the AJAX trigger button
    Then the AJAX content should be displayed

  @uitpg_regression @positive
  Scenario: UITPG-REG-AJX-02 AJAX result text is not empty after loading
    Given I navigate to the ajax data page
    When I click the AJAX trigger button
    Then the AJAX result text should not be empty

  @uitpg_regression @negative
  Scenario: UITPG-REG-AJX-03 AJAX result is not present before button is clicked
    Given I navigate to the ajax data page
    Then the AJAX result should not be present before clicking
