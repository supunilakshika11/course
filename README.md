# GearRent Pro

**GearRent Pro** is a JavaFX-based equipment rental management system developed for  
**CMJD 112 – Coursework 02**.

The system supports multi-branch equipment rentals with role-based access control,
pricing rules, and transaction management.

---

## ✅ Key Features
- User authentication (Admin / Branch Manager / Staff)
- Multi-branch equipment management
- Role-based authorization
- Rental quotation calculation
- Rental creation with validations
- Branch-level equipment restriction
- Transaction handling (commit / rollback)
- MySQL database integration
- JavaFX user interface

---

## 👤 User Roles & Permissions

| Role | Username | Permissions |
|------|----------|-------------|
| ADMIN | admin | System management (view-only) |
| BRANCH_MANAGER | pan_mgr | Issue rentals (PAN branch) |
| STAFF | gal_staff | Issue rentals (GAL branch) |

**Password for all demo users:** `1234`

---

## 🛠 Technology Stack
- **Language:** Java 17
- **UI:** JavaFX
- **Database:** MySQL (MAMP)
- **Build Tool:** Maven
- **IDE:** VS Code
- **Architecture:** MVC + Service + DAO

---

## ▶️ How to Run the Application

### Prerequisites
- Java JDK 17 installed
- Maven installed
- MySQL running (via MAMP)
- Database imported using `schema.sql`

### Run Commands
Open a terminal in the project root directory (where `pom.xml` is located) and run:

```bash
mvn clean compile
mvn javafx:run
```

##  Database Setup

###  Start MySQL
- Open **MAMP**
- Click **Start Servers**
- Ensure **MySQL is running (GREEN)**

###  MySQL Configuration
- Host: `localhost`
- Port: `8889`
- Username: `root`
- Password: `root`

###  Create Database
Import the SQL file provided in the project root.

**File:** `schema.sql`

Steps (phpMyAdmin):
1. Open `http://localhost:8888/phpMyAdmin`
2. Click **Import**
3. Select `schema.sql`
4. Click **Go**

This will create:
- Database: `gear_rent_pro`
- Tables
- Sample data

---

## ⚙️ Application Configuration

### DB Connection (`DB.java`)
```java
jdbc:mysql://localhost:8889/gear_rent_pro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo
```
