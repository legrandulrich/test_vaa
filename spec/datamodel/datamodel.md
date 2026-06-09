# Data Model

> Entity definitions and relationships. Evolves as features are added.

| Entity | Key Fields | Relationships |
|--------|-----------|-----------------|
| User | id, username, email, role, department | Has many Expenses (as submitter), Has many Approvals (as approver) |
| Expense | id, description, amount, category, status, submitted_date, currency | Belongs to User (submitter), Has many LineItems, Has many Receipts, Has many Approvals |
| LineItem | id, description, amount, category | Belongs to Expense |
| Receipt | id, file_path, file_name, upload_date | Belongs to Expense |
| Approval | id, status, approver_comment, reviewed_date | Belongs to User (approver), Belongs to Expense |
| ApprovalPolicy | id, name, approval_levels, amount_threshold | Referenced by Approval workflow |

## Status Enums

- **Expense Status:** Draft, Submitted, Pending Approval, Approved, Rejected, Reimbursed
- **Approval Status:** Pending, Approved, Rejected
- **User Role:** Employee, Manager, Admin
