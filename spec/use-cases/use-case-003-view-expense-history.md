# UC-003: View Expense History and Status

> **As an** employee, **I want to** view my submitted expenses and track their status through the approval process **so that** I can follow up on reimbursement and plan my finances.

**Status:** Pending
**Date:** 2024-01-15

---

## Main Flow

- I navigate to the "My Expenses" section from the main menu
- I see a list of all my expense reports with columns showing: description, amount, submission date, current status, and action buttons
- I can filter the list by status (Draft, Submitted, Approved, Rejected, Reimbursed)
- I click on an expense to view the full details including all line items and receipts
- In the detail view, I see a timeline showing the submission date, approval date, and any comments from my manager
- If rejected, I see the rejection reason and can click "Resubmit" to make changes and resubmit
- I can edit draft expenses and submit them when ready
- I can download the expense report as a PDF

---

## Business Rules

| ID | Rule |
|----|------|
| BR-301 | Employees can only see their own expense reports |
| BR-302 | Draft expenses can be edited and deleted by the submitter |
| BR-303 | Submitted expenses cannot be modified until approved or rejected |
| BR-304 | Rejected expenses can be resubmitted after editing |
| BR-305 | All expenses include a timeline showing key status transitions and approver comments |
| BR-306 | Expense history is retained indefinitely for audit purposes |

---

## Acceptance Criteria

- [ ] Employee sees a list of all their submitted expenses
- [ ] List displays expense summary: description, amount, date, status
- [ ] Employee can filter expenses by status (Draft, Submitted, Approved, Rejected, Reimbursed)
- [ ] Employee can click an expense to view full details including all line items
- [ ] Detail view shows attached receipts and approval timeline
- [ ] Employee can see manager comments and approval decisions
- [ ] Rejected expenses show rejection reason and have a "Resubmit" option
- [ ] Draft expenses can be edited and deleted
- [ ] Employee can download expense report as PDF

---

## Tests

- [ ] ViewExpenseHistoryTest — verifies list display and filtering
- [ ] ViewExpenseDetailTest — verifies detail view with line items and timeline
- [ ] EditDraftExpenseTest — verifies draft editing capability
- [ ] ResubmitRejectedExpenseTest — verifies resubmission of rejected expenses
- [ ] AccessControlTest — verifies employees only see their own expenses

---

## UI / Routes

The expense history interface includes a table view with filtering options and a detail modal or side panel showing complete expense information with an approval timeline.

| Route | Access | Notes |
|-------|--------|-------|
| `/my-expenses` | Authenticated (Employee) | @Route("my-expenses") — employee's expense list |
| `/my-expenses/{id}` | Authenticated (Employee) | @Route("my-expenses/:id") — expense detail view |
