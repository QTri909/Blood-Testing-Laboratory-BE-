package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.documents.MedicalRecordDocument;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.entities.MedicalRecordEntity;
import sum25.group03.patientservice.exception.medical.record.MedicalRecordNotFound;
import sum25.group03.patientservice.mapper.MedicalRecordMapper;
import sum25.group03.patientservice.repositories.mongo.MedicalRecordMongoRepository;
import sum25.group03.patientservice.services.interfaces.MedicalRecordMongoService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordMongoServiceImpl implements MedicalRecordMongoService {

    private final MedicalRecordMongoRepository medicalRecordMongoRepository;
    private final MedicalRecordMapper recordMapper;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public void createNewMedicalRecordInMongoDb(MedicalRecordEntity medicalRecordEntity) {
        MedicalRecordDocument document = recordMapper.toMedicalRecordDocument(medicalRecordEntity);
        medicalRecordMongoRepository.save(document);
        log.info("Medical record saved successfully to mongoDb!");
    }

    @Transactional
    public void updateMedicalRecord(UpdatedAssignedDoctor updatedAssignedDoctor) {

        Long medicalRecordId = updatedAssignedDoctor.getRecordId();
        Long newAssignedUserId = updatedAssignedDoctor.getAssignedUserId();
        Long updatedById = updatedAssignedDoctor.getUpdatedBy();

        // update fields
        final String RECORD_ID = "recordId";
        final String ASSIGNED_USER = "assignedUser";
        final String UPDATED_BY = "updatedBy";
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(RECORD_ID).is(medicalRecordId)),
                //--
                new Update().set(ASSIGNED_USER, newAssignedUserId)
                            .set(UPDATED_BY, updatedById),
                //--
                MedicalRecordDocument.class
        ); // update first matching document

        log.info("Medical record updated successfully to mongoDb!");
    }
}
