USE city_complaints;

-- 1. Display all complaints
SELECT * FROM Complaints;

-- 2. Display only resolved complaints
SELECT *
FROM Complaints
WHERE status = 'Resolved';

-- 3. Display high-priority complaints
SELECT *
FROM Complaints
WHERE priority = 'High';

-- 4. Find complaints containing the word 'water'
SELECT *
FROM Complaints
WHERE description LIKE '%water%';

-- 5. Find complaints belonging to selected categories
SELECT *
FROM Complaints
WHERE category_id IN (1, 2, 3);

-- 6. Find complaints with IDs between 1 and 3
SELECT *
FROM Complaints
WHERE complaint_id BETWEEN 1 AND 3;

-- 7. Display complaints in descending order of priority
SELECT *
FROM Complaints
ORDER BY priority DESC;

-- 8. Display complaints from oldest to newest
SELECT *
FROM Complaints
ORDER BY complaint_date ASC;

-- 9. Display unique complaint statuses
SELECT DISTINCT status
FROM Complaints;

-- 10. Count total number of complaints
SELECT COUNT(*) AS total_complaints
FROM Complaints;

-- 11. Find the highest complaint ID
SELECT MAX(complaint_id) AS highest_complaint_id
FROM Complaints;

-- 12. Find the lowest complaint ID
SELECT MIN(complaint_id) AS lowest_complaint_id
FROM Complaints;

-- 13. Find the average complaint ID
SELECT AVG(complaint_id) AS average_complaint_id
FROM Complaints;

-- 14. Count complaints based on their status
SELECT status, COUNT(*) AS complaint_count
FROM Complaints
GROUP BY status;

-- 15. Count complaints for each priority
SELECT priority, COUNT(*) AS complaint_count
FROM Complaints
GROUP BY priority;

-- 16. Count complaints for each status
SELECT status, COUNT(*) AS complaint_count
FROM Complaints
GROUP BY status;

-- 17. Display only statuses having more than one complaint
SELECT status, COUNT(*) AS complaint_count
FROM Complaints
GROUP BY status
HAVING COUNT(*) > 1;

-- 18. Display complaints with citizen names
SELECT
    c.complaint_id,
    ci.citizen_name,
    c.description,
    c.status
FROM Complaints c
INNER JOIN Citizens ci
    ON c.citizen_id = ci.citizen_id;


-- 19. Display complaint, citizen, category and department details
SELECT
    c.complaint_id,
    ci.citizen_name,
    cc.category_name,
    d.department_name,
    c.status
FROM Complaints c
INNER JOIN Citizens ci
    ON c.citizen_id = ci.citizen_id
INNER JOIN Complaint_Categories cc
    ON c.category_id = cc.category_id
INNER JOIN Departments d
    ON cc.department_id = d.department_id;


-- 20. Display complaints with assigned employees
SELECT
    c.complaint_id,
    c.description,
    e.employee_name,
    e.designation
FROM Complaints c
INNER JOIN Complaint_Assignments ca
    ON c.complaint_id = ca.complaint_id
INNER JOIN Employees e
    ON ca.employee_id = e.employee_id;

    -- 21. Find complaints having the highest complaint ID
SELECT *
FROM Complaints
WHERE complaint_id = (
    SELECT MAX(complaint_id)
    FROM Complaints
);


-- 22. Find complaints having the same priority as complaint 1
SELECT *
FROM Complaints
WHERE priority = (
    SELECT priority
    FROM Complaints
    WHERE complaint_id = 1
);


-- 23. Find citizens who have submitted complaints
SELECT citizen_id, citizen_name
FROM Citizens
WHERE citizen_id IN (
    SELECT citizen_id
    FROM Complaints
);

-- 24. Complete complaint report
SELECT
    c.complaint_id,
    ci.citizen_name,
    cc.category_name,
    d.department_name,
    e.employee_name,
    c.status,
    c.priority,
    c.complaint_date
FROM Complaints c
INNER JOIN Citizens ci
    ON c.citizen_id = ci.citizen_id
INNER JOIN Complaint_Categories cc
    ON c.category_id = cc.category_id
INNER JOIN Departments d
    ON cc.department_id = d.department_id
INNER JOIN Complaint_Assignments ca
    ON c.complaint_id = ca.complaint_id
INNER JOIN Employees e
    ON ca.employee_id = e.employee_id;


-- 25. Count complaints handled by each department
SELECT
    d.department_name,
    COUNT(c.complaint_id) AS total_complaints
FROM Departments d
INNER JOIN Complaint_Categories cc
    ON d.department_id = cc.department_id
INNER JOIN Complaints c
    ON cc.category_id = c.category_id
GROUP BY d.department_name;


-- 26. Count complaints based on status
SELECT
    status,
    COUNT(*) AS total_complaints
FROM Complaints
GROUP BY status
ORDER BY total_complaints DESC;