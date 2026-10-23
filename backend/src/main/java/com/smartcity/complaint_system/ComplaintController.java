
package com.smartcity.complaint_system;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ComplaintController {

    private final JdbcTemplate jdbcTemplate;

    public ComplaintController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
}
