@ui @uitpg
Feature: Class Attribute
  Buttons with multiple CSS classes must be located using XPath contains()
  or a CSS attribute selector rather than an exact class match.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-03 Class Attribute page loads with primary button visible
    Given I navigate to the class attribute page
    Then the class attribute page should be loaded
    And the primary button should be visible

  @uitpg_regression @positive
  Scenario: UITPG-REG-CLS-01 Click button using XPath contains()
    Given I navigate to the class attribute page
    Then the primary button should be visible
    When I click the primary button using XPath contains
    Then the primary button should remain on the page

  @uitpg_regression @positive
  Scenario: UITPG-REG-CLS-02 Button has multiple CSS classes including btn-primary
    Given I navigate to the class attribute page
    Then the button class attribute should contain "btn-primary"

  @uitpg_regression @positive
  Scenario: UITPG-REG-CLS-03 Click button using CSS selector alternative
    Given I navigate to the class attribute page
    When I click the primary button using CSS selector
    Then the primary button should remain on the page
