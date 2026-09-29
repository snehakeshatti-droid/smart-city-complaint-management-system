CREATE DATABASE IF NOT EXISTS city_complaints;
USE city_complaints;


-- 1. City Zones
CREATE TABLE City_Zones (
    zone_id INT AUTO_INCREMENT PRIMARY KEY,
    zone_name VARCHAR(50) NOT NULL UNIQUE,
    city_area VARCHAR(100) NOT NULL
);


-- 2. Departments
CREATE TABLE Departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    department_head VARCHAR(100) NOT NULL
);


-- 3. Citizens
CREATE TABLE Citizens (
    citizen_id INT AUTO_INCREMENT PRIMARY KEY,
    citizen_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(200) NOT NULL,
    zone_id INT NOT NULL,
    FOREIGN KEY (zone_id) REFERENCES City_Zones(zone_id)
);


-- 4. Employees
CREATE TABLE Employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    department_id INT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES Departments(department_id)
);


-- 5. Complaint Categories
CREATE TABLE Complaint_Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    department_id INT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES Departments(department_id)
);


-- 6. Complaints
CREATE TABLE Complaints (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    citizen_id INT NOT NULL,
    category_id INT NOT NULL,
    zone_id INT NOT NULL,
    description VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    priority VARCHAR(10) NOT NULL DEFAULT 'Medium',
    complaint_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_date DATETIME NULL,

    FOREIGN KEY (citizen_id) REFERENCES Citizens(citizen_id),
    FOREIGN KEY (category_id) REFERENCES Complaint_Categories(category_id),
    FOREIGN KEY (zone_id) REFERENCES City_Zones(zone_id),

    CHECK (status IN ('Pending', 'In Progress', 'Resolved')),
    CHECK (priority IN ('Low', 'Medium', 'High'))
);


-- 7. Complaint Assignments
CREATE TABLE Complaint_Assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    employee_id INT NOT NULL,
    assigned_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (complaint_id) REFERENCES Complaints(complaint_id),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);


-- 8. Complaint History
CREATE TABLE Complaint_History (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by VARCHAR(100) NOT NULL,
    change_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks VARCHAR(255),

    FOREIGN KEY (complaint_id) REFERENCES Complaints(complaint_id),

    CHECK (new_status IN ('Pending', 'In Progress', 'Resolved')),
    CHECK (
        old_status IS NULL
        OR old_status IN ('Pending', 'In Progress', 'Resolved')
    )
);