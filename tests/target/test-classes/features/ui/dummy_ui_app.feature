@ui @demo
Feature: Dummy UI app login

  Scenario: Successful login shows success message
    Given I open the dummy ui app
    When I login with username "kc" and password "sdet123"
    Then I should see message "Login successful"
