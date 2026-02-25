@ui @uitpg
Feature: UITestingPlayground Home Page
  Verify the home page loads and navigation links are available.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-01 Home page loads successfully
    Given I navigate to the UITestingPlayground home page
    Then the home page should be loaded

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-01b Navigation links are present on home page
    Given I navigate to the UITestingPlayground home page
    Then the navigation links should be present

  @uitpg_regression @positive
  Scenario: UITPG-REG-HOME-01 Home page header is visible
    Given I navigate to the UITestingPlayground home page
    Then the home page header should be visible
