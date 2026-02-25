@ui @uitpg
Feature: File Upload
  Files are uploaded by sending the absolute file path to the hidden file
  input element using sendKeys — no need to interact with OS file dialogs.

  @uitpg_smoke @positive
  Scenario: UITPG-SMOKE-20 File Upload page loads with file input visible
    Given I navigate to the file upload page
    Then the file upload page should be loaded
    And the file input element should be present

  @uitpg_regression @positive
  Scenario: UITPG-REG-FUP-01 Upload a file using sendKeys on file input
    Given I navigate to the file upload page
    When I upload a temp file with name "testfile" and content "This is a test file for automation"
    Then the file input element should be present

  @uitpg_regression @positive
  Scenario: UITPG-REG-FUP-02 File input element is present on the page
    Given I navigate to the file upload page
    Then the file input element should be present

  @uitpg_regression @negative
  Scenario: UITPG-REG-FUP-03 No filename shown before a file is selected
    Given I navigate to the file upload page
    Then no filename should be shown before file is selected
