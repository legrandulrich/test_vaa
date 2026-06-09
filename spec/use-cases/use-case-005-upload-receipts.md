# UC-005: Upload and View Receipt Documents

> **As an** employee, **I want to** attach receipt images or PDF files to my expense report **so that** I can provide documentation for my business expenses.

**Status:** Pending
**Date:** 2024-01-15

---

## Main Flow

- While creating or editing an expense report, I see a "Receipts" section
- I click "Upload Receipt" to open a file picker
- I select one or more image files (JPG, PNG) or PDF documents from my computer
- The system displays a preview of each uploaded file
- I can click on a thumbnail to view the full-size receipt image in a lightbox
- I can delete a receipt by clicking the remove icon
- When I save or submit the expense, the receipts are attached to the report
- Later, when viewing the expense detail, managers can see all attached receipts with previews

---

## Business Rules

| ID | Rule |
|----|------|
| BR-501 | Supported file types: JPEG, PNG, PDF (max 5 MB per file) |
| BR-502 | Maximum 10 receipts per expense report |
| BR-503 | Receipts are required for expenses over a configured threshold |
| BR-504 | File names are sanitized and stored with timestamps |
| BR-505 | Only the submitting employee can modify receipts before approval |
| BR-506 | Receipts cannot be deleted after expense is submitted |
| BR-507 | Uploaded files are stored within the application storage |

---

## Acceptance Criteria

- [ ] Employee can click "Upload Receipt" to open file picker
- [ ] System accepts JPEG, PNG, and PDF files
- [ ] System rejects files larger than 5 MB
- [ ] System displays preview thumbnails of uploaded files
- [ ] Employee can view full-size receipt in lightbox/modal
- [ ] Employee can delete a receipt before submission
- [ ] System displays error if maximum file count (10) is exceeded
- [ ] Employee can upload multiple receipts in a single session
- [ ] Receipts are visible to approvers when reviewing expense
- [ ] Receipts cannot be deleted after expense is submitted

---

## Tests

- [ ] UploadReceiptTest — verifies file upload and preview
- [ ] ReceiptValidationTest — verifies file type and size validation
- [ ] ReceiptStorageTest — verifies files are stored correctly
- [ ] ReceiptViewTest — verifies managers can view receipts during approval
- [ ] ReceiptAccessControlTest — verifies only submitter can edit pre-submission receipts

---

## UI / Routes

The receipt upload interface appears as a section within the expense form with drag-and-drop support and a file picker button. Receipt previews are displayed as thumbnails with delete buttons.

| Route | Access | Notes |
|-------|--------|-------|
| `/expenses/new` | Authenticated (Employee) | Includes receipt upload section |
| `/expenses/{id}` | Authenticated (Employee) | Edit receipts for draft expenses |
| `/approvals/{id}` | Authenticated (Manager/Admin) | View attached receipts |
