USE city_complaints;


-- 1. City Zones
INSERT INTO City_Zones (zone_name, city_area)
VALUES
('Zone 1', 'Central City'),
('Zone 2', 'North City'),
('Zone 3', 'South City'),
('Zone 4', 'East City'),
('Zone 5', 'West City');


-- 2. Departments
INSERT INTO Departments (department_name, department_head)
VALUES
('Roads and Infrastructure', 'Rajesh Kumar'),
('Water Supply', 'Priya Sharma'),
('Waste Management', 'Amit Patil'),
('Electricity', 'Suresh Rao'),
('Streetlight Services', 'Neha Joshi');


-- 3. Citizens
INSERT INTO Citizens
(citizen_name, phone_number, email, address, zone_id)
VALUES
('Aarav Sharma', '9876543210', 'aarav@gmail.com', 'Central City', 1),
('Priya Patil', '9876543211', 'priya@gmail.com', 'North City', 2),
('Rahul Joshi', '9876543212', 'rahul@gmail.com', 'South City', 3),
('Sneha Kulkarni', '9876543213', 'sneha@gmail.com', 'East City', 4),
('Rohan Deshmukh', '9876543214', 'rohan@gmail.com', 'West City', 5);


-- 4. Employees
INSERT INTO Employees
(employee_name, designation, phone_number, department_id)
VALUES
('Vikram Singh', 'Road Inspector', '9876500011', 1),
('Anjali Mehta', 'Water Engineer', '9876500012', 2),
('Kiran Pawar', 'Waste Management Officer', '9876500013', 3),
('Manish Rao', 'Electrical Engineer', '9876500014', 4),
('Pooja Nair', 'Streetlight Technician', '9876500015', 5);


-- 5. Complaint Categories
INSERT INTO Complaint_Categories
(category_name, department_id)
VALUES
('Road Damage', 1),
('Water Leakage', 2),
('Garbage Collection', 3),
('Power Failure', 4),
('Streetlight Not Working', 5);


-- 6. Complaints
INSERT INTO Complaints
(citizen_id, category_id, zone_id, description, status, priority)
VALUES
(1, 1, 1, 'Large pothole near the main road', 'Pending', 'High'),
(2, 2, 2, 'Water leakage from underground pipeline', 'In Progress', 'High'),
(3, 3, 3, 'Garbage has not been collected for three days', 'Pending', 'Medium'),
(4, 4, 4, 'Power supply is frequently interrupted', 'Resolved', 'High'),
(5, 5, 5, 'Streetlight is not working near the residential area', 'In Progress', 'Medium');


-- 7. Complaint Assignments
INSERT INTO Complaint_Assignments
(complaint_id, employee_id)
VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);


-- 8. Complaint History
INSERT INTO Complaint_History
(complaint_id, old_status, new_status, changed_by, remarks)
VALUES
(1, NULL, 'Pending', 'System', 'Complaint submitted'),
(2, 'Pending', 'In Progress', 'Admin', 'Complaint assigned for investigation'),
(3, NULL, 'Pending', 'System', 'Complaint submitted'),
(4, 'In Progress', 'Resolved', 'Employee', 'Complaint resolved successfully'),
(5, 'Pending', 'In Progress', 'Admin', 'Complaint assigned to employee');