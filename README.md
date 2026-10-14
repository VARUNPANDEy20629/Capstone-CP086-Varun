# Capstone-CP086-Varun
# 💪 FitTrack — Gym Membership and Access Management System

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-blue)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-C71A36?logo=apachemaven)
![Project Status](https://img.shields.io/badge/Status-In%20Development-yellow)

**FitTrack** is a Java-based gym management application designed to simplify membership administration, payment tracking, subscription management, and gym access verification.

The system provides separate interfaces for administrators and gym members, helping fitness centers manage their daily operations efficiently.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Objectives](#-objectives)
- [Key Features](#-key-features)
- [Technology Stack](#️-technology-stack)
- [System Architecture](#️-system-architecture)
- [Database Design](#️-database-design)
- [Project Structure](#-project-structure)
- [Installation and Setup](#️-installation-and-setup)
- [How It Works](#-how-it-works)
- [Security](#-security)
- [Future Enhancements](#-future-enhancements)
- [Contributors](#-contributors)
- [License](#-license)

---

## 📖 Overview

Managing gym memberships manually can lead to errors in payment records, membership expiry tracking, and attendance management.

FitTrack aims to solve these problems by providing a centralized digital platform for gym operations.

The application is designed to manage member registration, membership plans, payment records, and gym entry verification. It also provides an administrative dashboard for monitoring membership statistics and access history.

Fingerprint-based authentication can be integrated using compatible biometric hardware and its supported SDK.

## 🎯 Objectives

- Digitize gym membership registration and management.
- Provide separate access for administrators and members.
- Track membership validity and expiry dates.
- Maintain payment and transaction records.
- Verify membership eligibility before granting gym access.
- Maintain gym entry logs and attendance history.
- Provide useful membership statistics and reports.
- Explore biometric authentication for secure gym access.

## ✨ Key Features

### 👤 Member Management
- Member registration and login.
- Member profile management.
- Membership plan selection.
- Membership status tracking.
- Membership renewal.
- Payment history.

### 🛠️ Admin Dashboard
- View total registered members.
- Monitor active and expired memberships.
- Add, update, and manage member records.
- Create and manage membership plans.
- Review payment records.
- Monitor gym entry history.
- View membership and revenue statistics.

### 📅 Membership Management
- Monthly, quarterly, and annual membership plans.
- Membership start and expiry date tracking.
- Automatic membership status calculation.
- Expiry monitoring.
- Membership renewal management.

### 💳 Payment Management
- Maintain payment records.
- Track payment amounts and dates.
- View transaction history.
- Associate payments with memberships.
- Generate revenue summaries.

*Real online payments require integration with a suitable payment gateway.*

### 🔐 Gym Access Management
- Verify member identity.
- Check membership validity.
- Approve or reject access attempts.
- Record entry timestamps.
- Maintain access logs.

### 👆 Fingerprint Authentication
- Planned integration with a compatible external fingerprint scanner.
- Biometric identity verification.
- Membership validation after successful identification.
- Access approval or rejection based on membership status.

*Actual fingerprint functionality depends on compatible hardware, SDK availability, and successful implementation.*

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application logic |
| JavaFX | Desktop user interface |
| JavaFX CSS | UI styling |
| MySQL | Database management |
| JDBC | Database connectivity |
| Maven | Dependency and build management |
| Git | Version control |
| GitHub | Source code hosting |
| Biometric SDK | Fingerprint integration, if implemented |

---

## 🏗️ System Architecture

```text
                 FITTRACK SYSTEM
                        |
             +----------+----------+
             |                     |
       Admin Dashboard       Member Dashboard
             |                     |
             +----------+----------+
                        |
                JavaFX Controllers
                        |
                  Service Layer
                        |
             +----------+----------+
             |                     |
      Membership Service     Access Service
             |                     |
             +----------+----------+
                        |
                    JDBC Layer
                        |
                  MySQL Database
                        |
             +----------+----------+
             |          |          |
           Users    Memberships  Payments
                        |
                    Access Logs
