
package com.smartcity.complaint_system;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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

    // Get all complaints from MySQL
    @GetMapping("/api/complaints")
    public List<Map<String, Object>> getComplaints() {

        String sql =
            "SELECT c.complaint_id, ci.citizen_name, " +
            "cc.category_name, z.zone_name, c.description, " +
            "c.status, c.priority, c.complaint_date " +
            "FROM Complaints c " +
            "JOIN Citizens ci ON c.citizen_id = ci.citizen_id " +
            "JOIN Complaint_Categories cc ON c.category_id = cc.category_id " +
            "JOIN City_Zones z ON c.zone_id = z.zone_id " +
            "ORDER BY c.complaint_id";

        return jdbcTemplate.queryForList(sql);
    }

    // Update complaint status and save a log in MongoDB
    @PutMapping("/api/complaints/{id}/status")
    public Map<String, Object> updateComplaintStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> request) {

        String newStatus = request.get("status");

        // Validate the requested status
        if (newStatus == null ||
            !(newStatus.equals("Pending") ||
              newStatus.equals("In Progress") ||
              newStatus.equals("Resolved"))) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Status must be Pending, In Progress, or Resolved"
            );
        }

        // Get the current status from MySQL
        List<String> statuses = jdbcTemplate.query(
            "SELECT status FROM Complaints WHERE complaint_id = ?",
            (rs, rowNum) -> rs.getString("status"),
            id
        );

        if (statuses.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Complaint not found"
            );
        }

        String oldStatus = statuses.get(0);

        // Update MySQL only if the status has changed
        if (!newStatus.equals(oldStatus)) {

            String sql =
                "UPDATE Complaints SET status = ?, " +
                "resolved_date = CASE WHEN ? = 'Resolved' " +
                "THEN CURRENT_TIMESTAMP ELSE NULL END " +
                "WHERE complaint_id = ?";

            jdbcTemplate.update(sql, newStatus, newStatus, id);

            // Save the status change in MongoDB
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
}
