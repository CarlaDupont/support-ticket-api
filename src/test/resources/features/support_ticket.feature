Feature: Support ticket management

  Scenario: Create a valid support ticket
    Given the ticket system is empty
    When the user creates a support ticket with title "Computer issue" and priority "HIGH"
    Then the ticket is created
    And the ticket status is "OPEN"

  Scenario: Resolve a support ticket
    Given an open support ticket exists with title "Printer issue" and priority "MEDIUM"
    When the user changes the ticket status to "RESOLVED"
    Then the ticket status is updated to "RESOLVED"

  Scenario: Refuse to modify a resolved ticket
    Given a resolved support ticket exists with title "Old issue" and priority "LOW"
    When the user changes the ticket status to "IN_PROGRESS"
    Then a conflict error is returned

  Scenario: Consult a missing support ticket
    Given the ticket system is empty
    When the user requests ticket with id 999
    Then a not found error is returned