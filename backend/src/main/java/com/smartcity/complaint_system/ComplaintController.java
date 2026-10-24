
package com.smartcity.complaint_system;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class ComplaintController {

    private final JdbcTemplate jdbcTemplate;
    private final ComplaintLogRepository complaintLogRepository;

    public ComplaintController(
            JdbcTemplate jdbcTemplate,
            ComplaintLogRepository complaintLogRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.complaintLogRepository = complaintLogRepository;
    }

    // 1. Get all complaints from MySQL
    @GetMapping("/api/complaints")
    public List<Map<String, Object>> getComplaints() {

        String sql =
            "SELECT c.complaint_id, c.citizen_id, " +
            "ci.citizen_name, cc.category_name, z.zone_name, " +
            "c.description, c.status, c.priority, " +
            "c.complaint_date, c.resolved_date " +
            "FROM Complaints c " +
            "JOIN Citizens ci ON c.citizen_id = ci.citizen_id " +
            "JOIN Complaint_Categories cc " +
            "ON c.category_id = cc.category_id " +
            "JOIN City_Zones z ON c.zone_id = z.zone_id " +
            "ORDER BY c.complaint_id DESC";

        return jdbcTemplate.queryForList(sql);
    }

    // 2. Submit a new complaint
    @PostMapping("/api/complaints")
    public Map<String, Object> createComplaint(
            @RequestBody Map<String, Object> request) {

        String citizenName = getText(request, "citizen_name");
        String description = getText(request, "description");
        String priority = getText(request, "priority");

        Integer citizenId = getInteger(request, "citizen_id");
        Integer categoryId = getInteger(request, "category_id");
        Integer zoneId = getInteger(request, "zone_id");

        // Validate required fields
        if (citizenName == null || citizenName.isBlank()
                || description == null || description.isBlank()
                || citizenId == null
                || categoryId == null
                || zoneId == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Please provide citizen name, description, citizen ID, category ID, and zone ID"
            );
        }

        // Validate priority
        if (priority == null ||
                !(priority.equals("Low")
                || priority.equals("Medium")
                || priority.equals("High"))) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Priority must be Low, Medium, or High"
            );
        }

        // Verify citizen exists
        Integer citizenCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM Citizens WHERE citizen_id = ?",
            Integer.class,
            citizenId
        );

        if (citizenCount == null || citizenCount == 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid citizen ID"
            );
        }

        // Verify category exists
        Integer categoryCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM Complaint_Categories WHERE category_id = ?",
            Integer.class,
            categoryId
        );

        if (categoryCount == null || categoryCount == 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid category ID"
            );
        }

        // Verify zone exists
        Integer zoneCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM City_Zones WHERE zone_id = ?",
            Integer.class,
            zoneId
        );

        if (zoneCount == null || zoneCount == 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid zone ID"
            );
        }

        // Insert complaint into MySQL
        String sql =
            "INSERT INTO Complaints " +
            "(citizen_id, category_id, zone_id, description, " +
            "status, priority, complaint_date) " +
            "VALUES (?, ?, ?, ?, 'Pending', ?, CURRENT_TIMESTAMP)";

        jdbcTemplate.update(
            sql,
            citizenId,
            categoryId,
            zoneId,
            description,
            priority
        );

        // Get the newly generated complaint ID
        Integer complaintId = jdbcTemplate.queryForObject(
            "SELECT LAST_INSERT_ID()",
            Integer.class
        );

        return Map.of(
            "message", "Complaint submitted successfully",
            "complaint_id", complaintId,
            "citizen_name", citizenName,
            "status", "Pending",
            "priority", priority
        );
    }

    // 3. Update complaint status and log changes in MongoDB
    @PutMapping("/api/complaints/{id}/status")
    public Map<String, Object> updateComplaintStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> request) {

        String newStatus = request.get("status");

        if (newStatus == null ||
                !(newStatus.equals("Pending")
                || newStatus.equals("In Progress")
                || newStatus.equals("Resolved"))) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Status must be Pending, In Progress, or Resolved"
            );
        }

        List<String> statuses = jdbcTemplate.query(
            "SELECT status FROM Complaints WHERE complaint_id = ?",
            (rs, rowNum) -> rs.getString("status"),
            id
        );

        if (statuses.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Complaint not found"
            );
        }

        String oldStatus = statuses.get(0);

        if (!newStatus.equals(oldStatus)) {

            String sql =
                "UPDATE Complaints SET status = ?, " +
                "resolved_date = CASE " +
                "WHEN ? = 'Resolved' THEN CURRENT_TIMESTAMP " +
                "ELSE NULL END " +
                "WHERE complaint_id = ?";

            jdbcTemplate.update(sql, newStatus, newStatus, id);

            ComplaintLog log = new ComplaintLog(
                id,
                oldStatus,
                newStatus,
                LocalDateTime.now(),
                "Complaint status updated"
            );

            complaintLogRepository.save(log);
        }

        return Map.of(
            "message", "Complaint status updated successfully",
            "complaint_id", id,
            "old_status", oldStatus,
            "status", newStatus
        );
    }

    // Helper method: read text from request
    private String getText(
            Map<String, Object> request,
            String key) {

        Object value = request.get(key);

        return value == null ? null : value.toString().trim();
    }

    // Helper method: read numeric IDs from request
    private Integer getInteger(
            Map<String, Object> request,
            String key) {

        Object value = request.get(key);

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        return null;
    }
}
