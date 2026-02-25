@ui @uitpg
Feature: Overlapped Element
  An input field is partially covered by a fixed header. Scrolling the element
  into view before typing prevents ElementClickInterceptedException.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-17 Overlapped Element page loads
    Given I navigate to the overlapped element page
    Then the overlapped element page should be loaded

  @uitpg_regression @positive
  Scenario: UITPG-REG-OVL-01 Type in overlapped name field using JavaScript scroll
    Given I navigate to the overlapped element page
    When I enter "John Doe" in the overlapped name field using JavaScript scroll
    Then the overlapped name field should contain "John Doe"

  @uitpg_regression @positive
  Scenario: UITPG-REG-OVL-02 Type in overlapped ID field after scrolling
    Given I navigate to the overlapped element page
    When I enter "12345" in the overlapped ID field
    Then the overlapped ID field should contain "12345"

  @uitpg_regression @positive
  Scenario: UITPG-REG-OVL-03 Type in overlapped name field using Actions scroll
    Given I navigate to the overlapped element page
    When I enter "Jane Smith" in the overlapped name field using Actions scroll
    Then the overlapped name field should contain "Jane Smith"
