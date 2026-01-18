# Inventory Management System – Business Logic Specification

## 1. Purpose
This document defines the **business rules and workflows** for the Inventory Management backend system.
It describes **what the system must do**, not how it is technically implemented.

---

## 2. Core Concepts

### 2.1 Entities
- **Product** – Item tracked in inventory
- **Warehouse** – Physical location where stock exists
- **Inventory Balance** – Current on-hand quantity for a product at a warehouse
- **Inventory Transaction** – Immutable audit record of every stock movement
- **Purchase Order (PO)** – Incoming stock from suppliers
- **Sales Order (SO)** – Outgoing stock to customers
- **Inventory Adjustment** – Manual correction of stock
- **Inventory Transfer** – Movement of stock between warehouses

---

## 3. Golden Business Rules (Must Follow)

1. Inventory balances must never be edited directly.
2. Every stock change must create an Inventory Transaction.
3. No negative inventory unless explicitly allowed.
4. Status-driven workflows only.
5. Posted records are immutable.

---

## 4. Inventory Balance Logic

- Balance Key: (product_id, warehouse_id)
- New Balance = current_balance + delta
- Reject if balance < 0
- Create balance only for positive deltas

---

## 5. Inventory Transaction Logic

Required fields:
- Transaction Type
- Product
- Warehouse
- Quantity
- Unit Cost (for inbound)
- Reference Document
- Created By
- Timestamp
- Notes

Rules:
- Transactions are immutable
- Corrections are done via reversal transactions

---

## 6. Purchase Order Business Logic

Statuses:
DRAFT → SUBMITTED → PARTIALLY_RECEIVED → RECEIVED
DRAFT → CANCELLED

Rules:
- Must have supplier and lines
- Receiving creates inventory transactions
- Partial receiving allowed
- Status updated automatically

---

## 7. Sales Order Business Logic

Statuses:
DRAFT → CONFIRMED → PARTIALLY_SHIPPED → SHIPPED
CONFIRMED → CANCELLED

Rules:
- Stock must exist to ship
- Shipping reduces inventory
- Returns add inventory back

---

## 8. Inventory Adjustment Logic

Statuses:
DRAFT → POSTED → (immutable)

Rules:
- Reason required
- Positive or negative quantity allowed
- Creates adjustment transactions

---

## 9. Inventory Transfer Logic

Statuses:
DRAFT → SUBMITTED → COMPLETED

Rules:
- Source must have stock
- Two transactions per transfer (OUT + IN)

---

## 10. Costing (Average Cost)

new_avg_cost =
(old_qty × old_avg_cost + inbound_qty × inbound_cost)
÷ (old_qty + inbound_qty)

---

## 11. Validation Rules

- Qty > 0
- Active products & warehouses
- No invalid status transitions
- No negative inventory

---

## 12. V1 Scope

- Products
- Warehouses
- Inventory Balances
- Inventory Transactions
- Purchase Orders
- Adjustments
- Transfers

---

## 13. Core Principle

Inventory Balance = Sum of Inventory Transactions
