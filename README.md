# University ERP System (Java Swing & MySQL)

A role-based University ERP desktop application built with Java Swing and MySQL, designed to manage courses, enrollments, grading, and administration. The system uses a secure two-database architecture (Auth + ERP) inspired by the UNIX shadow password model and follows an MVC-aligned layered design.

## Highlights
- Role-based dashboards for Student, Instructor, and Admin
- Secure authentication with BCrypt-hashed passwords
- Separate Auth DB and ERP DB for improved security
- System-wide Maintenance Mode enforcing read-only access
- MVC-aligned layered architecture (UI → API → Service → DAO)
- CSV export for transcripts and gradebooks

## Core Features
**Student**
- Browse course catalog
- Register/drop sections with capacity and deadline checks
- View timetable and grades
- Export transcript (CSV)

**Instructor**
- Manage assigned sections only
- Enter assessment scores and compute final grades
- View basic class statistics
- Export gradebook (CSV)

**Admin**
- Create users, courses, and sections
- Assign instructors
- Toggle Maintenance Mode

## Architecture & Security
- Swing UI as View, domain models as Model, API/Service layers as Controllers
- Access rules and maintenance checks enforced at the service layer
- Passwords stored only as hashes in Auth DB; no credentials in ERP DB

## Tech Stack
Java (JDK 17+), Java Swing, MySQL, JDBC, BCrypt (jBCrypt), FlatLaf

## How to Run
- Import the provided SQL file into MySQL
- Configure DB connections:
  - Auth DB: `jdbc:mysql://localhost:3306/auth_db_2`
  - ERP DB: `jdbc:mysql://localhost:3306/erp_db_2`
- Run `LoginFrame.java` from `edu.univ.erp.ui.auth`

**Default Accounts**
- admin1 / admin1234 (Admin)
- inst1 / inst1234 (Instructor)
- stu1 / stu123 (Student)
- stu2 / stu123 (Student)

## Author
**Tanishk Singh Yadav**  

