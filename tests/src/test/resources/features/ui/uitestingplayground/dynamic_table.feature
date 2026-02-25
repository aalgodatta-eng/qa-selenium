@ui @uitpg
Feature: Dynamic Table
  Table rows and columns are generated dynamically. A warning label shows
  Chrome CPU usage that must match the value found in the table.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-11 Dynamic Table page loads with data rows
    Given I navigate to the dynamic table page
    Then the dynamic table page should be loaded
    And the table should have at least one row

  @uitpg_regression @positive
  Scenario: UITPG-REG-DYT-01 Chrome CPU in table matches the warning label
    Given I navigate to the dynamic table page
    Then the Chrome CPU value in the table should match the warning label

  @uitpg_regression @positive
  Scenario: UITPG-REG-DYT-02 Table has multiple rows of data
    Given I navigate to the dynamic table page
    Then the table should have at least one row

  @uitpg_regression @positive
  Scenario: UITPG-REG-DYT-03 Warning label contains a Chrome CPU percentage
    Given I navigate to the dynamic table page
    Then the warning label should contain a CPU percentage value

  @uitpg_regression @negative
  Scenario: UITPG-REG-DYT-04 Table and warning label are always in sync despite dynamic data
    Given I navigate to the dynamic table page
    Then the dynamic table and warning label should always be in sync
