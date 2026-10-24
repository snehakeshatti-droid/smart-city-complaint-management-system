
package com.smartcity.complaint_system;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComplaintLogRepository
        extends MongoRepository<ComplaintLog, String> {
}
