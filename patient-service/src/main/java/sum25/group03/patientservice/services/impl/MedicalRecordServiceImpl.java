package sum25.group03.patientservice.services.impl;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.groovy.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.documents.AuditEntryDocument;
import sum25.group03.patientservice.documents.MedicalRecordDocument;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.entities.MedicalRecordEntity;
import sum25.group03.patientservice.enums.DocumentType;
import sum25.group03.patientservice.exception.medical.record.MedicalRecordNotFound;
import sum25.group03.patientservice.exception.user.snapshot.UserNotFoundException;
import sum25.group03.patientservice.mapper.MedicalRecordMapper;
import sum25.group03.patientservice.repositories.mongo.MedicalRecordMongoRepository;
import sum25.group03.patientservice.repositories.postgres.MedicalRecordRepository;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.MedicalRecordMongoService;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final UserSnapshotRepository userSnapshotRepository;

    private final MedicalRecordMongoServiceImpl medicalRecordMongoService;
    private final AuditEntryMongoServiceImpl auditEntryMongoService;
    private final MedicalRecordMapper recordMapper;

    @Transactional
    public MedicalRecordResponse registerMedicalRecord(MedicalRecordRequest request) {

        // map request to entity
        MedicalRecordEntity entity = MedicalRecordEntity.builder()
                .patientId(request.patientId())
                .assignedUser(request.assignedUser())
                .createdBy(request.createdBy())
                .updatedBy(request.updatedBy())
                .build();

        // save to database
        medicalRecordRepository.save(entity);

        // save to mongoDb
        medicalRecordMongoService.createNewMedicalRecordInMongoDb(entity);

        return medicalRecordMapper.toMedicalRecordResponse(entity);
    }

    private MedicalRecordEntity updateAssignedDoctorValidator(Long recordId, Long assignedUserId, Long updatedById) {
        // retrieve record from database;
        MedicalRecordEntity entity = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new MedicalRecordNotFound("Medical record with id " + recordId + " not found!"));

        // specify assigned user existence
        boolean isAssignedUserExisted = userSnapshotRepository.existsByExternalUserId(assignedUserId);
        if (!isAssignedUserExisted)
            throw new UserNotFoundException("Save assigned user failed because assigned user is not found!");

        // prevent duplicated assignment
        if (entity.getAssignedUser().equals(assignedUserId))
            throw new IllegalArgumentException("Assigned user already exists!");

        // specify updated by user existence
        boolean isUpdatedByUserExisted = userSnapshotRepository.existsByExternalUserId(updatedById);
        if (!isUpdatedByUserExisted)
            throw new UserNotFoundException("Save updated user failed because updated user is not found!");

        return entity;
    }

    @Override
    @Transactional
    public UpdatedAssignedDoctor updateAssignedDoctor(UpdatedAssignedDoctor updateInfo) {
        Long recordId = updateInfo.getRecordId();
        Long assignedUserId = updateInfo.getAssignedUserId();
        Long updatedById = updateInfo.getUpdatedBy();

        // validation checks
        MedicalRecordEntity entity = updateAssignedDoctorValidator(recordId, assignedUserId, updatedById);

        // store old value for auditing:
        Long oldAssignedUserId = entity.getAssignedUser();

        // update information:
        entity.setUpdatedBy(updatedById);
        entity.setAssignedUser(assignedUserId);

        // logs the update action for auditing purposes
        // TODO: integrate with proper logging framework or auditing service to cloud watch AWS

        // update document, versioning in medical record mongoDb:
        medicalRecordMongoService.updateMedicalRecord(updateInfo);
        AuditEntryDocument auditEntryAssignedUser = AuditEntryDocument.builder()
                .entityId(recordId)
                .fieldChanged("assignedUser")
                .oldValue(oldAssignedUserId + "")
                .newValue(assignedUserId + "")
                .changedBy(updatedById)
                .entityType(DocumentType.MEDICAL_RECORD)
                .build();

        // track the change in audit entry collection in mongoDb
        auditEntryMongoService.saveAuditEntry(auditEntryAssignedUser);
        log.info("Audit entry saved successfully to mongoDb!");
        log.info("Saved document: {}", auditEntryAssignedUser);

        return updateInfo;
    }
}
