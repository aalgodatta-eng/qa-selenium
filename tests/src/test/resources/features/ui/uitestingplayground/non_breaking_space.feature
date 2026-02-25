@ui @uitpg
Feature: Non-Breaking Space
  Button text contains a non-breaking space (NBSP) character.
  XPath normalize-space() and contains() handle NBSP; plain text() matching fails.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-16 Non-Breaking Space page loads
    Given I navigate to the non-breaking space page
    Then the non-breaking space page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-NBS-01 Find button using normalize-space XPath
    Given I navigate to the non-breaking space page
    When I click the NBSP button using normalize-space XPath with text "My Button"
    Then the NBSP button should be found and clicked using normalize-space

  @uitpg_regression @negative
  Scenario: UITPG-REG-NBS-02 Regular space XPath may fail with non-breaking space text
    Given I navigate to the non-breaking space page
    When I search for the NBSP button with regular space text "My Button"
    Then the regular space XPath may fail with non-breaking space text

  @uitpg_regression @positive
  Scenario: UITPG-REG-NBS-03 contains() finds button regardless of space type
    Given I navigate to the non-breaking space page
    When I search for the NBSP button using contains XPath with text "Button"
    Then the NBSP button should be found regardless of space type
