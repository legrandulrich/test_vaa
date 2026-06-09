# UC-002: Approve or Reject Expense

> **As a** manager, **I want to** review and approve or reject expense submissions from my team **so that** I can control spending and ensure compliance with expense policies.

**Status:** Pending
**Date:** 2024-01-15

---

## Main Flow

- I navigate to the "Approvals" section from the main menu
- I see a list of pending expense reports awaiting my review
- I click on an expense to view full details including all line items and attached receipts
- I review the itemized breakdown and receipt documentation
- I can add a comment or note explaining my decision
- I click either "Approve" or "Reject"
- If approved, the system updates the expense status to "Approved" and notifies the employee
- If rejected, the system marks it as "Rejected" and notifies the employee with my comment
- I am returned to the approvals list and see the reviewed expense no longer appears as pending

---

## Business Rules

| ID | Rule |
|----|------|
| BR-201 | Only managers and admins can approve expenses |
| BR-202 | Expenses cannot be approved until they are in "Submitted" status |
| BR-203 | Each approval action (approve/reject) must be recorded with timestamp and approver identity |
| BR-204 | Approved expenses transition to "Approved" status |
| BR-205 | Rejected expenses transition to "Rejected" status and can be resubmitted by the employee |
| BR-206 | Approval comments are visible to the submitting employee |

---

## Acceptance Criteria

- [ ] Manager sees only pending expenses for their team members in the approvals list
- [ ] Manager can view complete expense details including line items and receipts
- [ ] Manager can add a comment or note before approving or rejecting
- [ ] Manager can approve an expense with a single click
- [ ] Manager can reject an expense with a reason/comment
- [ ] System records the approval decision with timestamp and approver name
- [ ] System updates the expense status based on the decision
- [ ] Employee is notified of the approval or rejection decision
- [ ] Rejected expenses can be resubmitted by the employee

---

## Tests

- [ ] ApproveExpenseTest — verifies expense approval flow
- [ ] RejectExpenseTest — verifies expense rejection with comments
- [ ] ApprovalNotificationTest — verifies employee notification on decision
- [ ] ApprovalAccessControlTest — verifies only managers/admins can access approvals

---

## UI / Routes

The approval interface displays a list of pending expenses with summary information, and a detail view showing full expense breakdown with receipt previews and comment input field.

| Route | Access | Notes |
|-------|--------|-------|
| `/approvals` | Authenticated (Manager/Admin) | @Route("approvals") — pending approvals list |
| `/approvals/{id}` | Authenticated (Manager/Admin) | @Route("approvals/:id") — approve/reject form |
