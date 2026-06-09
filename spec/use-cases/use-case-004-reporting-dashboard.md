# UC-004: View Spending Reports and Analytics

> **As an** administrator or manager, **I want to** view reports and dashboards showing expense data and spending patterns **so that** I can analyze departmental spending and ensure budget compliance.

**Status:** Pending
**Date:** 2024-01-15

---

## Main Flow

- I navigate to the "Reports" section from the main menu
- I see a dashboard with summary cards showing: total expenses submitted, total approved, total pending, and total reimbursed (all in current fiscal period)
- Below the summary, I see charts visualizing:
  - Spending by category (pie chart)
  - Spending by department (bar chart)
  - Spending trend over time (line chart)
- I can apply filters: date range, department, category, status
- I can select a specific employee to see their individual spending
- I can export the report data as CSV or PDF
- The dashboard refreshes to show updated data based on my filter selections

---

## Business Rules

| ID | Rule |
|----|------|
| BR-401 | Only managers and admins can access reporting dashboards |
| BR-402 | Managers see only their department's expenses |
| BR-403 | Admins see all expenses across all departments |
| BR-404 | Reports display data for the current fiscal year by default |
| BR-405 | All currency amounts are displayed in the system default currency |
| BR-406 | Charts update in real-time when filters are applied |

---

## Acceptance Criteria

- [ ] User sees a dashboard with summary metrics (total, approved, pending, reimbursed)
- [ ] Dashboard displays spending by category as a pie chart
- [ ] Dashboard displays spending by department as a bar chart
- [ ] Dashboard displays spending trends over time as a line chart
- [ ] User can filter by date range
- [ ] User can filter by department
- [ ] User can filter by expense category
- [ ] User can filter by expense status
- [ ] User can select an individual employee to view their spending
- [ ] Managers see only their department's data
- [ ] Admins see company-wide data
- [ ] User can export report as CSV
- [ ] User can export report as PDF
- [ ] Charts update when filters are applied

---

## Tests

- [ ] ReportingDashboardTest — verifies dashboard rendering with data
- [ ] ReportFilterTest — verifies filtering by various criteria
- [ ] ReportExportTest — verifies CSV and PDF export functionality
- [ ] ReportAccessControlTest — verifies manager and admin permissions
- [ ] ReportChartsTest — verifies chart data accuracy and updates

---

## UI / Routes

The reporting dashboard includes a summary section with KPI cards, followed by a filter bar and multi-chart layout. All charts are responsive and update when filters change.

| Route | Access | Notes |
|-------|--------|-------|
| `/reports` | Authenticated (Manager/Admin) | @Route("reports") — main reports dashboard |
| `/reports/export` | Authenticated (Manager/Admin) | Download/export endpoint |
