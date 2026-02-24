@ui @smoke @ui_smoke
Feature: Basic UI smoke

  Scenario: Open base URL and verify title exists
    Given I open the application
    Then the page title should not be empty
