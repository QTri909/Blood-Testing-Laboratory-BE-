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
    public List<EventLog> searchEventLogs(String action, String message, String operator) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (action != null && !action.isEmpty()) {
            criteriaList.add(Criteria.where("action").regex(action, "i")); // i = case-insensitive
        }
        if (message != null && !message.isEmpty()) {
            criteriaList.add(Criteria.where("message").regex(message, "i"));
        }
        if (operator != null && !operator.isEmpty()) {
            criteriaList.add(Criteria.where("operator").regex(operator, "i"));
        }

        Criteria criteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria);

        return mongoTemplate.find(query, EventLog.class);
    }
}
