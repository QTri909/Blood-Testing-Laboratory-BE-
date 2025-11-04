package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.documents.AuditEntryDocument;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.NewRecordStatusRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.entities.MedicalRecordEntity;
import sum25.group03.patientservice.enums.ActionTypeFeatures;
import sum25.group03.patientservice.enums.DocumentType;
import sum25.group03.patientservice.enums.MedicalRecordStatus;
import sum25.group03.patientservice.exception.medical.record.MedicalRecordNotFound;
import sum25.group03.patientservice.exception.user.snapshot.UserNotFoundException;
import sum25.group03.patientservice.mapper.MedicalRecordMapper;
import sum25.group03.patientservice.repositories.postgres.MedicalRecordRepository;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;
import sum25.group03.patientservice.grpc.TestOrderGrpcClient;
import sum25.group03.patientservice.grpc.TestOrderResponse;



import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final UserSnapshotRepository userSnapshotRepository;

    private final MedicalRecordMongoServiceImpl medicalRecordMongoService;
    private final AuditEntryMongoServiceImpl auditEntryMongoService;

    private final ActionLogService actionLogService;

    // check if a viewerId belongs to our system or not, if not, throw exception and warn to admin
    private void validateViewerExistence(Long actorId) {
        boolean isViewerExisted = userSnapshotRepository.existsByExternalUserId(actorId);
        if (!isViewerExisted) {
            log.warn("WARN: User with id {} has been found in the system!", actorId);
            throw new UserNotFoundException("User with id " + actorId + " not found in the system!");
        }
    }

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
        String logMessage = String.format(
                "Medical record with id %d assigned user updated from %d to %d by user %d",
                recordId, oldAssignedUserId, assignedUserId, updatedById
        );

        // update document and versioning in medical record mongoDb:
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

        return updateInfo;
    }

    @Override
    public MedicalRecordResponse getById(Long recordId, Long viewerId) {

        // validate viewer existence
        validateViewerExistence(viewerId);

        // log the view action
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_PATIENT_MEDICAL_RECORD_DETAIL, recordId);

        return medicalRecordRepository.findById(recordId)
                .map(medicalRecordMapper::toMedicalRecordResponse)
                .orElseThrow(() -> new RuntimeException("Medical Record not found"));
    }

    @Override
    public MedicalRecordResponse getByCode(UUID recordCode) {
        return medicalRecordRepository.findByRecordCode(recordCode)
                .map(medicalRecordMapper::toMedicalRecordResponse)
                .orElseThrow(() -> new RuntimeException("Medical Record not found"));
    }

    @Override
    public List<MedicalRecordResponse> getAll(Long viewerId) {

        // validate viewer existence
        validateViewerExistence(viewerId);

        // log the view all action
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_ALL_PATIENT_MEDICAL_RECORDS, null);

        return medicalRecordRepository.findAll()
                .stream()
                .map(medicalRecordMapper::toMedicalRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecordResponse> getByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .stream()
                .map(medicalRecordMapper::toMedicalRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(NewRecordStatusRequest newRecordStatusRequest) {
        // extract info:
        Long recordId = newRecordStatusRequest.recordId();
        Long deleterId = newRecordStatusRequest.updatedBy();
        MedicalRecordStatus newStatus = newRecordStatusRequest.newStatus();

        // validate deleter existence
        validateViewerExistence(deleterId);

        // retrieve record from database
        MedicalRecordEntity entity = medicalRecordRepository.findByRecordIdAndStatusNot(
                recordId, MedicalRecordStatus.DELETED
        ).orElseThrow(() -> new RuntimeException("Medical Record not found!"));

        // soft delete from database, and log the delete action
        MedicalRecordStatus oldStatus = entity.getStatus();
        entity.setStatus(newStatus);
        entity.setUpdatedBy(deleterId);
        actionLogService.logAction(deleterId, ActionTypeFeatures.DELETE_PATIENT_MEDICAL_RECORD, recordId);

        // update mongodb medical record and audit info for mongoDb
        medicalRecordMongoService.updateMedicalRecordStatus(newRecordStatusRequest);
        AuditEntryDocument auditEntryStatusChange = AuditEntryDocument.builder()
                .entityId(recordId)
                .fieldChanged("status")
                .oldValue(oldStatus.name())
                .newValue(newStatus.name())
                .changedBy(deleterId)
                .entityType(DocumentType.MEDICAL_RECORD)
                .build();
        auditEntryMongoService.saveAuditEntry(auditEntryStatusChange);
    }

}
