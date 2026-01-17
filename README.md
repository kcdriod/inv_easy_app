# 📦 Inventory Management Database

## 📌 Overview
This project defines a **simple and normalized Inventory Management database schema** using **PostgreSQL**.  
It supports product tracking, warehouse stock management, inventory transactions, suppliers, and purchase orders.

---

## 🗄️ Database Details
- **Database Name**: mydb
- **Schema Name**: inventory_management
- **Database Type**: PostgreSQL

---

## 📂 Schema Initialization

```sql
CREATE SCHEMA IF NOT EXISTS inventory_management;
SET search_path TO inventory_management;
```

---

## 📊 Tables Description

### product_categories
Stores product category information.

Columns:
- category_id (PK)
- name
- description

---

### products
Stores product master data.

Columns:
- product_id (PK)
- sku
- name
- description
- category_id (FK)
- unit_price
- is_active
- created_at

---

### warehouses
Represents warehouse locations.

Columns:
- warehouse_id (PK)
- name
- location
- is_active

---

### inventory_balances
Maintains current stock per product and warehouse.

Columns:
- product_id (PK, FK)
- warehouse_id (PK, FK)
- quantity_on_hand
- updated_at

---

### inventory_transactions
Tracks all inventory movements.

Columns:
- txn_id (PK)
- txn_type
- product_id (FK)
- warehouse_id (FK)
- quantity
- reference_no
- notes
- created_at

---

### suppliers
Stores supplier details.

Columns:
- supplier_id (PK)
- name
- phone
- email

---

### purchase_orders
Stores purchase order headers.

Columns:
- po_id (PK)
- supplier_id (FK)
- po_date
- status

---

### purchase_order_lines
Stores line items for purchase orders.

Columns:
- po_line_id (PK)
- po_id (FK)
- product_id (FK)
- quantity
- unit_cost

---

## 🔗 Relationships Summary
- products → product_categories
- inventory_balances → products, warehouses
- inventory_transactions → products, warehouses
- purchase_orders → suppliers
- purchase_order_lines → purchase_orders, products

---

## 🚀 Usage
Designed for integration with:
- Spring Boot
- JPA / Hibernate
- REST APIs
- PostgreSQL

---

## ✅ Status
Schema and tables are ready for application integration.
