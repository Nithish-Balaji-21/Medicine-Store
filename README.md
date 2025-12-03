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

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/Nithish-Balaji-21/Pharmacy_Management.git
cd Pharmacy_Management
```

### 2. Configure Database

Create a MySQL database and set environment variables:

**Windows (PowerShell):**
```powershell
$env:DB_URL = "jdbc:mysql://localhost/medicalstore"
$env:DB_USER = "root"
$env:DB_PASSWORD = "your_password"
```

**Linux/Mac (Bash):**
```bash
export DB_URL="jdbc:mysql://localhost/medicalstore"
export DB_USER="root"
export DB_PASSWORD="your_password"
```

Or update `db/DBConnection.java` with your credentials.

### 3. Compile the Project
```bash
javac -d . db/DBConnection.java ui/*.java
```

### 4. Run the Application
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

## License

MIT License

## Author

Nithish Balaji
