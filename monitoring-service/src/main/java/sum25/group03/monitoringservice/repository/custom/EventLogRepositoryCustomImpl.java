package sum25.group03.monitoringservice.repository.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sum25.group03.monitoringservice.model.EventLog;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EventLogRepositoryCustomImpl implements EventLogRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<EventLog> searchEventLogs(String action, String message, String operator, String sourceService) {
        List<Criteria> criteriaList = new ArrayList<>();

        // Search by action prefix
        if (action != null && !action.isEmpty()) {
            // Lấy prefix (ví dụ CREATE_ROLE -> CREATE)
            String prefix = action.split("_")[0];
            criteriaList.add(Criteria.where("action").regex("^" + prefix, "i")); // ^ = bắt đầu bằng
        }

        // Search by message (case-insensitive)
        if (message != null && !message.isEmpty()) {
            criteriaList.add(Criteria.where("message").regex(message, "i"));
        }

        // Search by operator (case-insensitive)
        if (operator != null && !operator.isEmpty()) {
            criteriaList.add(Criteria.where("operator").regex(operator, "i"));
        }

        // Search by sourceService (case-insensitive)
        if (sourceService != null && !sourceService.isEmpty()) {
            criteriaList.add(Criteria.where("sourceService").regex(sourceService, "i"));
        }

        Criteria criteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria);

        return mongoTemplate.find(query, EventLog.class);
    }
}
