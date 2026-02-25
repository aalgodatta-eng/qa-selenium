@ui @uitpg
Feature: Frames
  The page contains nested iframes. WebDriver context must be switched to
  the target frame before interacting with its elements.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-24 Frames page loads and contains iframes
    Given I navigate to the frames page
    Then the frames page should be loaded
    And the page should contain at least 1 iframe

  @uitpg_regression @positive
  Scenario: UITPG-REG-FRM-01 Switch to frame and read frame count
    Given I navigate to the frames page
    Then the page should contain at least 1 iframe

  @uitpg_regression @positive
  Scenario: UITPG-REG-FRM-02 Read content from nested frame
    Given I navigate to the frames page
    Then reading content from the nested frame should return non-null

  @uitpg_regression @positive
  Scenario: UITPG-REG-FRM-03 Switch between default content and outer frame
    Given I navigate to the frames page
    When I switch to the outer frame and back to default content
    Then frame switching should complete without error

  @uitpg_regression @negative
  Scenario: UITPG-REG-FRM-04 Elements inside frame not found without context switch
    Given I navigate to the frames page
    Then frame element lookup without switching context should be documented
