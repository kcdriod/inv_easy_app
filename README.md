# 🧾 Inventory Management System — Business Logic Specification (V1)

> ✅ **Purpose:** Define **business rules and workflows** for the Inventory Management backend system.  
> ❌ This document describes **what the system must do**, not how it is technically implemented.

---

## 📌 Table of Contents

- [1. Purpose](#1-purpose)
- [2. Core Concepts](#2-core-concepts)
  - [2.1 Key Entities](#21-key-entities)
- [3. Golden Business Rules](#3-golden-business-rules)
- [4. Inventory Balance Logic](#4-inventory-balance-logic)
- [5. Inventory Transaction Logic](#5-inventory-transaction-logic)
- [6. Purchase Order (PO) Business Logic](#6-purchase-order-po-business-logic)
- [7. Sales Order (SO) Business Logic](#7-sales-order-so-business-logic)
- [8. Inventory Adjustment Logic](#8-inventory-adjustment-logic)
- [9. Inventory Transfer Logic](#9-inventory-transfer-logic)
- [10. Costing Rule — Average Cost](#10-costing-rule--average-cost)
- [11. Validation Rules](#11-validation-rules)
- [12. V1 Scope](#12-v1-scope)
- [13. Core Principle](#13-core-principle)

---

## 1. Purpose

The Inventory Management System ensures:

- ✅ Accurate tracking of stock across warehouses
- ✅ Full audit trail for every inventory movement
- ✅ Status-based workflows for PO/SO/Transfers/Adjustments
- ✅ Prevention of invalid or inconsistent inventory states

⬆️ [Back to TOC](#-table-of-contents)

---

## 2. Core Concepts

### 2.1 Key Entities

| Entity | Description |
|-------|-------------|
| **Product** | Item tracked in inventory |
| **Warehouse** | Physical storage location |
| **Inventory Balance** | Current on-hand qty of a product in a warehouse |
| **Inventory Transaction** | Immutable audit record of every movement |
| **Purchase Order (PO)** | Stock coming **IN** from supplier |
| **Sales Order (SO)** | Stock going **OUT** to customer |
| **Inventory Adjustment** | Manual correction of stock |
| **Inventory Transfer** | Move stock between warehouses |

⬆️ [Back to TOC](#-table-of-contents)

---

## 3. Golden Business Rules

> These rules are **non-negotiable** and must always be enforced.

✅ Rules:

1. **Inventory balances must never be edited directly**
2. **Every stock change must generate an Inventory Transaction**
3. **No negative inventory**, unless explicitly enabled
4. **All workflows are status-driven**
5. **Posted/Completed records are immutable**
6. **Corrections are done via reversal transactions**, not edits

⬆️ [Back to TOC](#-table-of-contents)

---

## 4. Inventory Balance Logic

### Balance Identity
**Balance Key** = `(product_id, warehouse_id)`

### Balance Formula
✅ **New Balance = current_balance + delta**

### Balance Enforcement Rules
- Reject transaction if `new_balance < 0`
- Create balance record only if:
  - delta is **positive**
  - and record does not exist

⬆️ [Back to TOC](#-table-of-contents)

---

## 5. Inventory Transaction Logic

Inventory Transactions are the **ledger** (single source of truth).

### 5.1 Required Fields

| Field | Required | Notes |
|------|----------|------|
| Transaction Type | ✅ | IN / OUT / ADJUST / TRANSFER |
| Product | ✅ | Must be active |
| Warehouse | ✅ | Must be active |
| Quantity | ✅ | Must be > 0 |
| Unit Cost | ✅ | Required for inbound transactions |
| Reference Document | ✅ | PO / SO / Adjustment / Transfer |
| Created By | ✅ | User |
| Timestamp | ✅ | Server time |
| Notes | Optional | Free text |

### 5.2 Rules
- ✅ Transactions are immutable
- ✅ No update/delete allowed
- ✅ Corrections require reversal transaction

⬆️ [Back to TOC](#-table-of-contents)

---

## 6. Purchase Order (PO) Business Logic

### 6.1 Status Flow

```text
DRAFT → SUBMITTED → PARTIALLY_RECEIVED → RECEIVED
DRAFT → CANCELLED
```

### 6.2 Business Rules
✅ PO must have:
- Supplier
- At least 1 line
- Qty > 0 per line

✅ Receiving:
- Creates `IN` Inventory Transactions
- Partial receiving is allowed
- Status auto-updates based on progress

⬆️ [Back to TOC](#-table-of-contents)

---

## 7. Sales Order (SO) Business Logic

### 7.1 Status Flow

```text
DRAFT → CONFIRMED → PARTIALLY_SHIPPED → SHIPPED
CONFIRMED → CANCELLED
```

### 7.2 Business Rules
- ✅ SO must be confirmed before shipping
- ✅ Stock must exist to ship
- ✅ Shipping reduces inventory (`OUT` transaction)
- ✅ Returns increase inventory (`IN` transaction)

⬆️ [Back to TOC](#-table-of-contents)

---

## 8. Inventory Adjustment Logic

### 8.1 Status Flow

```text
DRAFT → POSTED → (immutable)
```

### 8.2 Business Rules
✅ Adjustment requires:
- Reason
- At least 1 line

✅ Adjustments allow:
- Positive delta (increase)
- Negative delta (decrease)

✅ Posting creates:
- Adjustment Inventory Transactions
- Balance updates via delta

🚫 Once POSTED:
- cannot be edited
- reversal transactions only

⬆️ [Back to TOC](#-table-of-contents)

---

## 9. Inventory Transfer Logic

### 9.1 Status Flow

```text
DRAFT → SUBMITTED → COMPLETED
```

### 9.2 Business Rules
✅ Transfer requires:
- from_warehouse_id
- to_warehouse_id
- at least 1 line with qty > 0

✅ Stock check:
- Source warehouse must have sufficient stock
- Reject if it causes negative inventory

### 9.3 Transaction Generation
A transfer generates **two transactions per line**:

| Transaction | Warehouse | Type |
|------------|----------|------|
| OUT | Source | TRANSFER_OUT |
| IN | Destination | TRANSFER_IN |

⬆️ [Back to TOC](#-table-of-contents)

---

## 10. Costing Rule — Average Cost

System uses **Weighted Average Cost**.

### 10.1 Formula

```text
new_avg_cost =
(old_qty × old_avg_cost + inbound_qty × inbound_cost)
÷ (old_qty + inbound_qty)
```

### 10.2 Costing Rules
✅ Avg cost updated only on inbound stock:
- PO receiving
- Returns
- Positive adjustments

🚫 Avg cost does NOT change for:
- Shipping
- Transfers out
- Transfers in (usually cost move)

⬆️ [Back to TOC](#-table-of-contents)

---

## 11. Validation Rules

### 11.1 Quantity Rules
- Line qty must be `> 0`
- Adjustment deltas may be positive or negative

### 11.2 Master Data Rules
- Product must be active
- Warehouse must be active
- Supplier / customer must exist where applicable

### 11.3 Workflow Rules
- No invalid status transitions
- Cannot post/complete without validations
- Posted records are immutable

### 11.4 Inventory Rules
- No negative inventory (default: disallow)

⬆️ [Back to TOC](#-table-of-contents)

---

## 12. V1 Scope

✅ Included in V1:

- Products
- Warehouses
- Inventory Balances
- Inventory Transactions
- Purchase Orders (Receiving)
- Inventory Adjustments
- Inventory Transfers

🚫 Not included in V1:

- Reservations / allocations
- FIFO/LIFO costing
- Multi-currency pricing
- Batch/Lot/Serial tracking
- Expiry-based stock tracking

⬆️ [Back to TOC](#-table-of-contents)

---

## 13. Core Principle

> ✅ **Inventory Balance = Sum of Inventory Transactions**

Meaning:

- Inventory Transactions = Ledger (truth)
- Inventory Balance = derived state
- No manual edits to balances ever
- Only transactions can change stock state

⬆️ [Back to TOC](#-table-of-contents)

---

✅ **End of Document**
