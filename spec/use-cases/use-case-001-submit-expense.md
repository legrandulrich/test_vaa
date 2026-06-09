# UC-001: Submit Expense Report

> **As an** employee, **I want to** submit an expense report with itemized details and receipts **so that** I can request reimbursement for business expenses.

**Status:** Pending
**Date:** 2024-01-15

---

## Main Flow

- I navigate to the Expenses section from the main menu
- I click "New Expense Report"
- I see a form with fields for expense description, amount, category, and date
- I add line items to the report (description, amount, category for each item)
- I upload receipt images or PDF files as documentation
- I review the total amount calculated from all line items
- I click "Submit for Approval"
- The system confirms the submission and shows my report in "Submitted" status
- I am returned to the expenses list and see my new report with a "Pending Approval" badge

---

## Business Rules

| ID | Rule |
|----|------|
| BR-001 | All required fields (description, amount, category) must be filled before submission |
| BR-002 | Expense amount must be greater than zero |
| BR-003 | At least one line item is required per expense report |
| BR-004 | Receipts are optional but recommended for amounts over a configured threshold |
| BR-005 | Submitted expenses cannot be edited; only drafts can be modified |
| BR-006 | Users can save incomplete reports as drafts without submitting |

---

## Acceptance Criteria

- [ ] Employee can create a new expense report in draft status
- [ ] Employee can add multiple line items with description, amount, and category
- [ ] Employee can upload receipt files (images or PDF)
- [ ] System validates that all required fields are populated before submission
- [ ] System calculates and displays the total expense amount
- [ ] Employee can save as draft without submitting
- [ ] Employee can submit the expense report for approval
- [ ] System changes status to "Submitted" and notifies the assigned approver
- [ ] Submitted expenses cannot be edited by the submitter

---

## Tests

- [ ] SubmitExpenseTest — verifies expense creation with valid data
- [ ] SubmitExpenseValidationTest — verifies required field validation
- [ ] SubmitExpenseReceiptTest — verifies receipt upload functionality
- [ ] SubmitExpenseDraftTest — verifies draft save functionality

---

## UI / Routes

The expense submission interface includes a form-based layout with two sections: expense details and line items. Users can dynamically add or remove line items before submission.

| Route | Access | Notes |
|-------|--------|-------|
| `/expenses` | Authenticated (Employee) | @Route("expenses") — main expenses list |
| `/expenses/new` | Authenticated (Employee) | @Route("expenses/new") — new expense form |
| `/expenses/{id}` | Authenticated (Employee) | @Route("expenses/:id") — view/edit expense |
