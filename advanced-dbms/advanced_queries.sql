USE city_complaints;

-- 1. Create a view for complaint details
CREATE VIEW Complaint_Details_View AS
SELECT
    c.complaint_id,
    ci.citizen_name,
    cc.category_name,
    d.department_name,
    c.description,
    c.status,
    c.priority,
    c.complaint_date
FROM Complaints c
INNER JOIN Citizens ci
    ON c.citizen_id = ci.citizen_id
INNER JOIN Complaint_Categories cc
    ON c.category_id = cc.category_id
INNER JOIN Departments d
    ON cc.department_id = d.department_id;

    -- 2. Stored procedure to find complaints by status

DELIMITER //

CREATE PROCEDURE GetComplaintsByStatus(IN complaint_status VARCHAR(20))
BEGIN
    SELECT
        complaint_id,
        description,
        status,
        priority,
        complaint_date
    FROM Complaints
    WHERE status = complaint_status;
END //

DELIMITER ;

-- 3. Function to return total number of complaints

DELIMITER //

CREATE FUNCTION GetTotalComplaints()
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE total INT;

    SELECT COUNT(*)
    INTO total
    FROM Complaints;

    RETURN total;
END //

DELIMITER ;

-- 4. Cursor to display all complaint IDs and statuses

DELIMITER //

CREATE PROCEDURE DisplayComplaintStatuses()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE c_id INT;
    DECLARE c_status VARCHAR(20);

    DECLARE complaint_cursor CURSOR FOR
        SELECT complaint_id, status
        FROM Complaints;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN complaint_cursor;

    read_loop: LOOP
        FETCH complaint_cursor INTO c_id, c_status;

        IF done = 1 THEN
            LEAVE read_loop;
        END IF;

        SELECT c_id AS complaint_id, c_status AS status;
    END LOOP;

    CLOSE complaint_cursor;
END //

DELIMITER ;

-- 5. Trigger to automatically maintain complaint history

DELIMITER //

CREATE TRIGGER Complaint_Status_History
AFTER UPDATE ON Complaints
FOR EACH ROW
BEGIN
    IF OLD.status <> NEW.status THEN
        INSERT INTO Complaint_History
        (
            complaint_id,
            old_status,
            new_status,
            changed_by,
            remarks
        )
        VALUES
        (
            NEW.complaint_id,
            OLD.status,
            NEW.status,
            USER(),
            'Complaint status updated'
        );
    END IF;
END //

DELIMITER ;