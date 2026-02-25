@ui @uitpg
Feature: Scrollbars
  A button is placed below the visible viewport. JavaScript scrollIntoView
  is used to bring it into view before clicking.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-10 Scrollbars page loads
    Given I navigate to the scrollbars page
    Then the scrollbars page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-SCR-01 Scroll button into viewport and click it
    Given I navigate to the scrollbars page
    When I scroll to the scrollable button and click it
    Then the scrollable button should be clickable after scrolling

  @uitpg_regression @positive
  Scenario: UITPG-REG-SCR-02 Button is in viewport after scrolling
    Given I navigate to the scrollbars page
    Then the scrollable button should be in the viewport

  @uitpg_regression @positive
  Scenario: UITPG-REG-SCR-03 Scroll to top and bottom of page without error
    Given I navigate to the scrollbars page
    When I scroll to the top and bottom of the scrollbars page
    Then the scroll operations should complete without error
