# Capstone-CP086-Varun & Sujan 
# 💪 FitTrack

### Gym Membership and Access Management System

*Track every membership. Protect every renewal. Control every entry.*

![Project ID](https://img.shields.io/badge/Project%20ID-CP--086-6f42c1?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Foundation-1f6feb?style=for-the-badge)
![Domain](https://img.shields.io/badge/Domain-Media%2C%20Sports%20%26%20Entertainment-6e7781?style=for-the-badge)

![Java](https://img.shields.io/badge/Java-17%2B-1f6feb?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/UI-JavaFX-6f42c1)
![MySQL](https://img.shields.io/badge/Database-MySQL-1f6feb?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-6e7781?logo=apachemaven&logoColor=white)
![Status](https://img.shields.io/badge/Status-In%20Development-6f42c1)

---

**FitTrack** is a Java-based desktop application for managing gym memberships, payments, and entry control. It was built for **CP-086 (Gym Membership and Access Management System)** and replaces manual record keeping with a centralized digital platform. It gives gym owners reliable tracking of memberships and gives members a simple way to manage their own subscriptions.

> **Project status:** FitTrack is **in development**. This README describes the intended design and architecture. Features marked *planned* or *optional* are not claimed as implemented. Update the status notes as components are completed and verified.

---

## 📑 Table of Contents

1. [Overview and Problem Statement](#-overview-and-problem-statement)
2. [Core Features](#-core-features)
3. [Membership and Access Rules](#-membership-and-access-rules)
4. [How It Works](#-how-it-works)
5. [Technology Stack](#️-technology-stack)
6. [System Architecture](#️-system-architecture)
7. [Database Design](#️-database-design)
8. [Algorithms and Technical Concepts](#-algorithms-and-technical-concepts)
9. [Business Metrics](#-business-metrics)
10. [Security](#-security)
11. [Getting Started](#-getting-started)
12. [Future Enhancements](#-future-enhancements)
13. [Learning Outcomes](#-learning-outcomes)
14. [Contributing, License and Conclusion](#-contributing-license-and-conclusion)

---

## 📖 Overview and Problem Statement

### The problem

A gym owner loses revenue because **renewals are missed** and **attendance is not tracked**. Gyms that rely on notebooks or spreadsheets face recurring issues: payment records contain errors, expiries go unnoticed, and members with lapsed plans can still walk in.

### The proposed solution

FitTrack stores membership plans, registers members with a start date, and calculates expiry dates automatically. It verifies membership validity before granting entry, logs every access attempt, and gives the owner dashboards for expiring memberships, revenue, and active members.

### Objectives

| # | Objective |
|---|-----------|
| 1 | Digitize gym membership registration and management |
| 2 | Provide separate interfaces for administrators and members |
| 3 | Track membership validity and expiry dates automatically |
| 4 | Maintain payment and transaction records |
| 5 | Verify eligibility before granting access and keep entry logs |
| 6 | Provide membership statistics and revenue reports |
| 7 | Explore biometric (fingerprint) authentication for secure access |

---

## ✨ Core Features

The five compulsory CP-086 features are listed first, followed by supporting features.

### CP-086 core requirements

| # | Feature | Description |
|---|---------|-------------|
| 1 | **Membership plans** | Store plans (monthly, quarterly, annual) with duration and price |
| 2 | **Member registration** | Register members with a plan and start date; expiry is calculated automatically |
| 3 | **Expiry monitoring** | List members whose plan expires within 7 days |
| 4 | **Check-ins and access control** | Record daily check-ins and block expired members |
| 5 | **Reporting** | Show monthly revenue and the number of active members |

### Supporting features

| Area | Capabilities |
|------|--------------|
| **Member dashboard** | Registration and login, profile management, plan selection, renewal, payment history |
| **Admin dashboard** | Total members, active versus expired counts, member and plan management, payment review, entry history, revenue statistics |
| **Payment records** | Amounts, dates, transaction history, payments linked to memberships, revenue summaries |
| **Access management** | Identity verification, validity check, approve or reject, entry timestamps, access logs |

> Real online payments would require integration with a suitable payment gateway, which is **not** part of the current scope.

### Optional and stretch features

| Feature | Status |
|---------|--------|
| Fingerprint authentication | **Planned.** Depends on compatible hardware, SDK availability, and successful implementation |
| Trainer assignment and per-trainer member list | **Not implemented** (CP-086 stretch) |
| Membership freeze for up to 30 days | **Not implemented** (CP-086 stretch) |

---

## 📅 Membership and Access Rules

| Rule | Definition |
|------|------------|
| **Expiry date** | Start date + plan duration |
| **Active member** | Today is on or before the expiry date |
| **Expired member** | Today is after the expiry date |
| **Expiring soon** | Expiry date falls between today and today + 7 days |
| **Check-in decision** | Allowed only for active members; expired members are blocked |
| **Logging** | Every attempt, allowed or denied, is recorded with a timestamp |

> The 7-day alert window is a **configurable design decision** and can be changed to suit the gym's renewal process.

---

## 🔄 How It Works

```mermaid
flowchart TD
    A([Start]) --> B[Admin creates membership plans]
    B --> C[Member registers and selects a plan]
    C --> D[Payment recorded]
    D --> E[Expiry date calculated: start date + duration]
    E --> F[Member arrives at the gym]
    F --> G[Identity verified: login or fingerprint if implemented]
    G --> H{Today on or before expiry date?}
    H -- Yes --> I[Allow entry]
    H -- No --> J[Block entry and prompt renewal]
    I --> K[Log access attempt]
    J --> K
    K --> L[Admin reviews dashboard: expiries, entries, revenue]
    L --> M([End])

    style A fill:#6f42c1,color:#ffffff,stroke:#6f42c1
    style M fill:#6f42c1,color:#ffffff,stroke:#6f42c1
    style E fill:#1f6feb,color:#ffffff,stroke:#1f6feb
    style H fill:#1f6feb,color:#ffffff,stroke:#1f6feb
    style K fill:#1f6feb,color:#ffffff,stroke:#1f6feb
```

### Step summary

1. An admin creates plans with a duration and price.
2. A member registers, chooses a plan and start date, and a payment is recorded.
3. The system calculates the expiry date and membership status.
4. At the gym, the member's identity is verified.
5. The Access Service checks validity, allows or blocks entry, and logs the attempt.
6. The admin monitors expiries, entry history, active members, and revenue.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|------------|---------|
| **Java 17+** | Application logic |
| **JavaFX** | Desktop user interface |
| **JavaFX CSS** | UI styling |
| **MySQL** | Persistent relational data storage |
| **JDBC** | Database connectivity |
| **Maven** | Dependency and build management |
| **Git** | Version control |
| **GitHub** | Source code hosting |
| **Biometric SDK** | Fingerprint integration, **only if implemented** |

---

## 🏗️ System Architecture

FitTrack uses a layered design that separates interface, business rules, and data access.

| Layer | Component | Responsibility |
|-------|-----------|----------------|
| Presentation | Admin Dashboard, Member Dashboard | User interaction |
| Control | JavaFX Controllers | Handle UI events and call services |
| Business Logic | Membership Service | Plans, registration, expiry, renewal, revenue |
| Business Logic | Access Service | Eligibility check, allow or block, access logging |
| Connectivity | JDBC Layer | Queries and transactions |
| Persistence | MySQL | Users, Memberships, Payments, Access Logs |

```mermaid
flowchart TB
    subgraph UI[Presentation Layer]
        AD[Admin Dashboard]
        MD[Member Dashboard]
    end
    subgraph CT[Control Layer]
        JC[JavaFX Controllers]
    end
    subgraph SV[Service Layer]
        MS[Membership Service]
        AS[Access Service]
    end
    subgraph DA[Connectivity Layer]
        JD[JDBC Layer]
    end
    subgraph DB[(MySQL Database)]
        U[Users]
        M[Memberships]
        P[Payments]
        L[Access Logs]
    end

    AD --> JC
    MD --> JC
    JC --> MS
    JC --> AS
    MS --> JD
    AS --> JD
    JD --> U
    JD --> M
    JD --> P
    JD --> L

    style AD fill:#6f42c1,color:#ffffff,stroke:#6f42c1
    style MD fill:#6f42c1,color:#ffffff,stroke:#6f42c1
    style JC fill:#1f6feb,color:#ffffff,stroke:#1f6feb
    style MS fill:#1f6feb,color:#ffffff,stroke:#1f6feb
    style AS fill:#1f6feb,color:#ffffff,stroke:#1f6feb
    style JD fill:#6e7781,color:#ffffff,stroke:#6e7781
```

> This diagram shows the **intended architecture**. It has not been verified against a completed implementation.

---

## 🗄️ Database Design

The schema below is a **suggested design**. Adjust names and types to match your final implementation. Membership plans are stored in their own table so that plan changes do not rewrite history.

| Table | Purpose | Key columns |
|-------|---------|-------------|
| `users` | Admins and members | `user_id`, `name`, `email`, `phone`, `password_hash`, `role` |
| `membership_plans` | Plan catalogue | `plan_id`, `name`, `duration_days`, `price` |
| `memberships` | A member's subscription | `membership_id`, `user_id`, `plan_id`, `start_date`, `expiry_date`, `status` |
| `payments` | Money received | `payment_id`, `membership_id`, `amount`, `payment_date` |
| `access_logs` | Entry attempts | `log_id`, `user_id`, `attempt_time`, `allowed`, `reason` |

```sql
CREATE DATABASE IF NOT EXISTS fittrack;
USE fittrack;

CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER'
);

CREATE TABLE membership_plans (
    plan_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50)   NOT NULL,
    duration_days INT           NOT NULL CHECK (duration_days > 0),
    price         DECIMAL(10,2) NOT NULL CHECK (price >= 0)
);

CREATE TABLE memberships (
    membership_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT  NOT NULL,
    plan_id       INT  NOT NULL,
    start_date    DATE NOT NULL,
    expiry_date   DATE NOT NULL,
    status        ENUM('ACTIVE', 'EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (plan_id) REFERENCES membership_plans(plan_id)
);

CREATE TABLE payments (
    payment_id    INT AUTO_INCREMENT PRIMARY KEY,
    membership_id INT           NOT NULL,
    amount        DECIMAL(10,2) NOT NULL,
    payment_date  DATE          NOT NULL,
    FOREIGN KEY (membership_id) REFERENCES memberships(membership_id)
);

CREATE TABLE access_logs (
    log_id       INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT         NOT NULL,
    attempt_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    allowed      TINYINT(1)  NOT NULL,
    reason       VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

> Whether a membership is currently active should be **calculated from `expiry_date` and today's date**, not trusted from a stored flag alone, because a stored status can become stale.

---

## 🧠 Algorithms and Technical Concepts

| Concept | Application in FitTrack |
|---------|-------------------------|
| **Date arithmetic** | Expiry = start date + plan duration |
| **Rule-based access control** | Entry depends on whether the membership is active |
| **Range filtering** | "Expiring soon" selects expiry dates within the next 7 days |
| **Aggregation** | Monthly revenue as a sum of payments; active members as a count |
| **CRUD operations** | Manage users, plans, memberships, and payments |
| **Audit logging** | Record every allowed and denied entry attempt |
| **Layered design** | Controllers, services, and JDBC kept separate |

### Core logic (pseudocode)

```text
expiry_date = start_date + plan.duration_days

is_active   = (today <= expiry_date)

expiring_soon = (today <= expiry_date) and (expiry_date <= today + 7 days)

on check-in:
    if is_active: allow, log(allowed = true)
    else:         block, log(allowed = false, reason = "Membership expired")
```

### Edge cases to handle

- The **expiry day itself** counts as active.
- Month-end and leap-year arithmetic when durations are expressed in months.
- Comparing **dates** versus **date-times** consistently.
- Renewals before expiry, which should extend from the current expiry rather than from today.

---

## 📊 Business Metrics

| Metric | Definition |
|--------|------------|
| **Monthly revenue** | Sum of `payments.amount` where `payment_date` falls within the chosen calendar month |
| **Active members** | Count of distinct members with at least one membership where `expiry_date >= today` |
| **Expiring soon** | Members whose active membership expires within the next 7 days |

### Example queries

```sql
-- Monthly revenue for October 2026
SELECT COALESCE(SUM(amount), 0) AS monthly_revenue
FROM payments
WHERE payment_date >= '2026-10-01'
  AND payment_date <  '2026-11-01';

-- Number of currently active members
SELECT COUNT(DISTINCT user_id) AS active_members
FROM memberships
WHERE expiry_date >= CURDATE();

-- Members expiring within 7 days
SELECT u.name, u.phone, m.expiry_date
FROM memberships m
JOIN users u ON u.user_id = m.user_id
WHERE m.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY);
```

> **Revenue and active members are different measures.** Revenue counts money received in a period, while active members counts people with valid plans today. A large annual payment raises revenue in one month but keeps the member active for twelve.

---

## 🔐 Security

| Area | Approach |
|------|----------|
| **Role separation** | Administrators and members see different interfaces and permissions |
| **Password storage** | Store salted hashes (for example BCrypt), never plain text |
| **SQL injection** | Use `PreparedStatement` for all database queries |
| **Credentials** | Keep database credentials out of source control |
| **Biometric data** | If fingerprint support is built, follow the SDK vendor's guidance and avoid storing raw biometric images |

> The measures above are the **intended practices**. Verify each against the final code before describing it as implemented.

---

## 🚀 Getting Started

> Replace all placeholders in angle brackets with your actual details.

### Prerequisites

- JDK 17 or later
- Apache Maven 3.8+
- MySQL Server 8.x
- Git
- An IDE with Java and JavaFX support

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repository>.git
cd <your-repository>
```

### 2. Set up the database

```bash
mysql -u <your-mysql-user> -p
```

Run the SQL from the [Database Design](#️-database-design) section to create the `fittrack` database and its tables.

### 3. Configure the database connection

Set your connection details in the project's configuration location (for example `src/main/resources/db.properties` or a constants class):

```properties
db.url=jdbc:mysql://localhost:3306/fittrack
db.user=<your-mysql-user>
db.password=<your-mysql-password>
```

> Do not commit real credentials. Add the configuration file to `.gitignore` if it contains secrets.

### 4. Build and run

```bash
mvn clean install
mvn javafx:run
```

`mvn javafx:run` assumes the JavaFX Maven plugin is configured in `pom.xml`. If you use a different run method, replace this step with the one your project uses.

---

## 🔭 Future Enhancements

| Enhancement | Description |
|-------------|-------------|
| Fingerprint authentication | Integrate a compatible scanner and SDK for biometric entry |
| Payment gateway | Accept real online payments |
| Expiry notifications | Email or SMS reminders before a plan lapses |
| Trainer assignment | Assign trainers and list each trainer's members (CP-086 stretch) |
| Membership freeze | Pause a membership for up to 30 days and extend expiry (CP-086 stretch) |
| Attendance analytics | Visit frequency, peak hours, and inactive members |
| Reports and charts | Exportable and visual revenue and membership reports |

---

## 🎓 Learning Outcomes

| Area | Concept demonstrated |
|------|----------------------|
| Layered architecture | Separation of UI, controllers, services, and data access |
| Database design | Normalised tables, keys, and constraints |
| JDBC and SQL | Connecting Java to MySQL, joins, filtering, and aggregation |
| Date handling | Expiry calculation, boundary cases, and range queries |
| Access rules | Turning business rules into testable conditions |
| Business reporting | Monthly aggregation and the difference between revenue and active members |
| Desktop UI | Building role-based screens with JavaFX |
| Engineering practice | Maven builds, Git workflow, and honest status documentation |

---

## 🤝 Contributing, License and Conclusion

### Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/<short-description>`
3. Commit with a clear message: `git commit -m "Add <feature>"`
4. Push the branch and open a pull request describing your changes.

### Contributors

- `<Your Name>` — `<your GitHub profile link>`

### License

Select an appropriate open-source license (for example MIT or Apache 2.0) and add it as a `LICENSE` file in the repository root. Update this section once chosen.

### Conclusion

FitTrack turns scattered membership records into a clear, reliable system: fewer missed renewals, tracked attendance, and visible revenue.

**Every member tracked. Every renewal protected.**
