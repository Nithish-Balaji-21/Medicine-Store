# Pharmacy Management System

A full-stack desktop application for medical inventory management with role-based authentication, real-time billing, and comprehensive reporting features.

## Features

- **Role-based Authentication** (Admin/Cashier)
- **Medicine Inventory Management**
- **Billing & Invoice Generation**
- **Stock Reporting**
- **Daily/Monthly Sales Reports**
- **Dashboard Analytics**

## Technology Stack

- **Language:** Java
- **GUI:** Swing
- **Database:** MySQL
- **Driver:** JDBC (MySQL Connector)

## Prerequisites

- Java 8 or higher
- MySQL Server
- MySQL JDBC Driver (mysql-connector-java-8.0.33.jar)

### Compile the Project
```bash
javac -d . db/DBConnection.java ui/*.java
```

### Run the Application
```bash
java -cp . ui.LoginForm
```

## Demo Credentials

For testing without a database:
- **Username:** `admin` or `cashier`
- **Password:** `admin123` or `cashier123`

## Project Structure

```
├── db/
│   └── DBConnection.java       # Database connection handler
├── ui/
│   ├── LoginForm.java          # Main entry point
│   ├── Dashboard.java          # Main dashboard
│   ├── MedicineForm.java       # Medicine management
│   ├── BillingForm.java        # Billing system
│   ├── InvoiceHistory.java     # Invoice records
│   ├── StockReport.java        # Stock tracking
│   ├── DailyMonthlyReport.java # Sales reports
│   └── InvoiceSummaryDialog.java
├── .gitignore
└── README.md
```
