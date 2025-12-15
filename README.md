# University ERP System (Java Swing & MySQL)

A role-based University ERP desktop application built using Java Swing and MySQL, designed to manage courses, class sections, enrollments, grading, and user administration in a secure and structured way. The system enforces strict access control, maintenance mode, and secure authentication using a two-database (Auth + ERP) architecture inspired by the UNIX “shadow password” model.The system follows a layered architecture aligned with MVC principles, where Swing UI acts as the View, domain models represent the Model, and API/Service layers function as Controllers.


## Key Highlights
- Role-based dashboards for Student, Instructor, and Admin
- Two separate databases:
  - Auth DB for usernames, roles, and password hashes
  - ERP DB for academic data (students, courses, sections, enrollments, grades)
- Secure password handling using BCrypt hashing
- System-wide Maintenance Mode with read-only enforcement
- Clean layered architecture (UI → API → Service → DAO)
- CSV exports for transcripts and gradebooks
- Desktop application built using Java Swing

## User Roles and Features

### Student
- Browse course catalog (code, title, credits, capacity, instructor)
- Register for sections with capacity and duplicate checks
- Drop sections before the deadline
- View personalized timetable
- View assessment-wise grades and final grade
- Download transcript as CSV

### Instructor
- View only assigned sections
- Enter assessment scores (Quiz, Midterm, End-Sem)
- Compute final grades using configurable weighting rules
- View class statistics (average, minimum, maximum)
- Export section gradebook as CSV

### Admin
- Create users (Students and Instructors) with secure credentials
- Create, edit, and manage courses and sections
- Assign instructors to sections
- Toggle Maintenance Mode
- Configure registration and drop deadlines

## Authentication and Security
- Passwords are never stored in plaintext
- Only BCrypt password hashes are stored in the Auth database
- ERP database contains no authentication data
- Login flow:
  1. User credentials are verified from the Auth DB
  2. User session stores user ID and role
  3. Profile data is loaded from ERP DB using shared user ID
- Login lockout after five consecutive failed attempts
- Change Password functionality is available

## System Architecture
The project follows a clean layered architecture:
UI Layer handles all Java Swing components
API Layer acts as a boundary between UI and services
Service Layer contains business logic and access control
Data Layer uses JDBC DAOs for MySQL
Auth Layer manages login, hashing, and sessions
Domain Layer contains plain data models
Utility Layer provides CSV export and helper utilities

## Package Structure
edu.univ.erp
- ui (Swing UI components)
- api (UI to Service boundary)
- service (business logic)
- data (JDBC DAOs for ERP DB)
- auth (authentication and password hashing)
- domain (entity models)
- util (CSV export and helpers)
- access (role and maintenance checks)

## Database Design

Auth Database (auth_db)
- users_auth(user_id, username, role, password_hash, status, last_login)

ERP Database (erp_db)
- students(user_id, roll_no, program, year)
- instructors(user_id, department)
- courses(course_id, code, title, credits)
- sections(section_id, course_id, instructor_id, day_time, room, capacity, semester, year)
- enrollments(enrollment_id, student_id, section_id, status)
- grades(grade_id, enrollment_id, component, score, final_grade)
- settings(setting_key, setting_value)

## Technologies Used
- Java (JDK 17 or higher)
- Java Swing
- MySQL
- JDBC
- BCrypt (jBCrypt)
- FlatLaf

## How to Run

Requirements
- Java JDK 17 or higher
- MySQL Server
- Required libraries: mysql-connector-j, jbcrypt, flatlaf

Database Setup
- Import the provided SQL file using MySQL CLI or any client

Database Configuration
Auth DB: jdbc:mysql://localhost:3306/auth_db_2
ERP DB: jdbc:mysql://localhost:3306/erp_db_2
Username: root
Password: your_mysql_password

Default Login Accounts
admin1 / admin1234 (Admin)
inst1 / inst1234 (Instructor)
stu1 / stu123 (Student)
stu2 / stu123 (Student)

Running the Application
- Open LoginFrame.java from edu.univ.erp.ui.auth
- Run the file from your IDE


## Author
Tanishk Singh Yadav

Java | Swing | MySQL | Desktop Application Development

