package sum25.group03.monitoringservice.repository.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<EventLog> searchEventLogs(
            String action,
            String message,
            String operator,
            String sourceService,
            Pageable pageable
    ) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (action != null && !action.isEmpty()) {
            String prefix = action.split("_")[0];
            criteriaList.add(Criteria.where("action").regex("^" + prefix, "i"));
        }

        if (message != null && !message.isEmpty()) {
            criteriaList.add(Criteria.where("message").regex(message, "i"));
        }

        if (operator != null && !operator.isEmpty()) {
            criteriaList.add(Criteria.where("operator").regex(operator, "i"));
        }

        if (sourceService != null && !sourceService.isEmpty()) {
            criteriaList.add(Criteria.where("sourceService").regex(sourceService, "i"));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, EventLog.class);

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));

        List<EventLog> results = mongoTemplate.find(query, EventLog.class);
        return new PageImpl<>(results, pageable, total);
    }
}
