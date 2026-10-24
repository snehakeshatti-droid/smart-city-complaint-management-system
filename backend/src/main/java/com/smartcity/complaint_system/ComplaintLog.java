
package com.smartcity.complaint_system;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "complaint_logs")
public class ComplaintLog {

    @Id
    private String id;

    private int complaintId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime changeDate;
    private String remarks;

    public ComplaintLog() {
    }

    public ComplaintLog(int complaintId, String oldStatus,
                        String newStatus, LocalDateTime changeDate,
                        String remarks) {
        this.complaintId = complaintId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changeDate = changeDate;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public String getRemarks() {
        return remarks;
    }
}
