CREATE DATABASE IF NOT EXISTS auth_db_2;
USE auth_db_2;

CREATE TABLE IF NOT EXISTS users_auth (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    role ENUM('Student','Instructor','Admin') NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status ENUM('active','inactive') DEFAULT 'active',
    last_login DATETIME
);

INSERT INTO users_auth (user_id, username, role, password_hash, status, last_login) VALUES
(1, 'admin1', 'Admin', '$2a$10$NnzdtL.96m2Km9D7ScArFu2EFxvpY2B7I/0U/viuFCErzppWX517W', 'active', NULL),
(2, 'inst1', 'Instructor', '$2a$10$rw7iBAjwe6wQ7dgT0K//g.hIl7OVFV.rBNLoC6nrs3.FcrlV.Lbn2', 'active', NULL),
(3, 'stu1', 'Student', '$2a$10$vk3xD0Z5777f26qXrPPd/uH57BKdoTvlZqrd.q/vrpafvSsozdQuO', 'active', NULL),
(4, 'stu2', 'Student', '$2a$10$x3fbE2F4ZFSiuwmjGQDwJeqwyoneCOuWJ6fD907aQ4cV1cKk128nm', 'active', NULL);

CREATE DATABASE IF NOT EXISTS erp_db_2;
USE erp_db_2;

CREATE TABLE IF NOT EXISTS students (
    user_id INT PRIMARY KEY,
    roll_no VARCHAR(20),
    program VARCHAR(50),
    year INT
);

INSERT INTO students (user_id, roll_no, program, year) VALUES
(3, 'CS101', 'B.Tech', 2),
(4, 'CS102', 'B.Tech', 3);

CREATE TABLE IF NOT EXISTS instructors (
    user_id INT PRIMARY KEY,
    department VARCHAR(50)
);

INSERT INTO instructors (user_id, department) VALUES
(2, 'Computer Science');

CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) UNIQUE,
    title VARCHAR(100),
    credits INT
);

INSERT INTO courses (course_id, code, title, credits) VALUES
(1, 'CS101', 'Introduction to Programming', 4);

CREATE TABLE IF NOT EXISTS sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT,
    instructor_id INT,
    day_time VARCHAR(50),
    room VARCHAR(20),
    capacity INT,
    semester VARCHAR(20),
    year INT
);

INSERT INTO sections (section_id, course_id, instructor_id, day_time, room, capacity, semester, year) VALUES
(1, 1, 2, 'Mon 10:00', 'B101', 40, 'Spring', 2025);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    section_id INT,
    status ENUM('active','dropped') DEFAULT 'active'
);

INSERT INTO enrollments (enrollment_id, student_id, section_id, status) VALUES
(1, 3, 1, 'active'),
(2, 4, 1, 'active');

CREATE TABLE IF NOT EXISTS grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT,
    component VARCHAR(50),
    score DECIMAL(5,2),
    final_grade VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(50)
);

INSERT INTO settings (setting_key, setting_value) VALUES
('maintenance_mode', 'OFF'),
('register_deadline', '2025-12-31'),
('drop_deadline', '2025-12-31');
