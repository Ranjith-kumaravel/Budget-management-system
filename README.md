# 💰 Budget Management System (Java AWT + MySQL)

## 📋 Project Description
The **Budget Management System** is a desktop application developed using **Java AWT** and **MySQL**.  
It allows users to efficiently manage their personal or organizational budget by recording and tracking **income and expenses** through an interactive graphical user interface.

---

## 🎯 Features
- Add, update, delete, and view budget entries  
- Record income and expense details  
- Search entries by category or description  
- View all transactions in a structured list  
- Store all data securely in a MySQL database  

---

## 🧠 Technologies Used
- **Frontend:** Java AWT (Abstract Window Toolkit)  
- **Backend:** MySQL  
- **Database Connector:** JDBC (Java Database Connectivity)  

---

## ⚙️ Requirements
- **Java JDK 8 or above**  
- **MySQL Server 8.0 or above**  
- **MySQL Connector/J (JAR file)**  

---

## 🗄️ Database Setup
1. Open MySQL Workbench or Command Line.
2. Create a database:
   ```sql
   CREATE DATABASE budgetdb;
   USE budgetdb;
   CREATE TABLE budget_entries (
       id INT AUTO_INCREMENT PRIMARY KEY,
       date DATE,
       category VARCHAR(100),
       description TEXT,
       amount DOUBLE,
       type VARCHAR(50)
   );
