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
import sum25.group03.monitoringservice.model.RawTestResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RawTestResultRepositoryCustomImpl implements RawTestResultRepositoryCustom{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<RawTestResult> searchRawTests(
            String testOrderId,
            String instrumentId,
            String status,
            String barcode,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (testOrderId != null && !testOrderId.isEmpty()) {
            criteriaList.add(Criteria.where("testOrderId").is(testOrderId));
        }
        if (instrumentId != null && !instrumentId.isEmpty()) {
            criteriaList.add(Criteria.where("instrumentId").is(instrumentId));
        }
        if (status != null && !status.isEmpty()) {
            criteriaList.add(Criteria.where("status").is(status));
        }
        if (barcode != null && !barcode.isEmpty()) {
            criteriaList.add(Criteria.where("barcode").is(barcode));
        }
        if (from != null) {
            criteriaList.add(Criteria.where("receivedAt").gte(from));
        }
        if (to != null) {
            criteriaList.add(Criteria.where("receivedAt").lte(to));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList));
        }

        long total = mongoTemplate.count(query, RawTestResult.class);

        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "receivedAt"));

        List<RawTestResult> results = mongoTemplate.find(query, RawTestResult.class);

        return new PageImpl<>(results, pageable, total);
    }
}
