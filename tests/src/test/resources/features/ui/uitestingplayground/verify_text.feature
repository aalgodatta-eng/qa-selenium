@ui @uitpg
Feature: Verify Text
  Text content may include non-standard whitespace or special characters.
  XPath normalize-space() and contains() are more robust than exact text() matches.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-12 Verify Text page loads and text container is visible
    Given I navigate to the verify text page
    Then the verify text page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-VTX-01 Text is verifiable using contains() XPath
    Given I navigate to the verify text page
    Then the displayed text should not be empty

  @uitpg_regression @positive
  Scenario: UITPG-REG-VTX-02 normalize-space handles extra whitespace in XPath
    Given I navigate to the verify text page
    Then normalize-space should handle whitespace in the displayed text

  @uitpg_regression @negative
  Scenario: UITPG-REG-VTX-03 Exact text match may fail due to whitespace differences
    Given I navigate to the verify text page
    Then the text whitespace sensitivity behaviour should be documented
