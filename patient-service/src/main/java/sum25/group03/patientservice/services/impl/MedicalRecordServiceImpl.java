package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import sum25.group03.patientservice.documents.AuditEntryDocument;
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
import sum25.group03.patientservice.services.interfaces.UserSnapshotService;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final UserSnapshotRepository userSnapshotRepository;

    private final MedicalRecordMongoServiceImpl medicalRecordMongoService;
    private final AuditEntryMongoServiceImpl auditEntryMongoService;

    private final ActionLogService actionLogService;
    private final UserSnapshotService userSnapshotService;

    // check if a viewerId belongs to our system or not, if not, throw exception and warn to admin
    private void validateViewerExistence(Long actorId) {
        boolean isViewerExisted = userSnapshotRepository.existsByExternalUserId(actorId);
        if (!isViewerExisted) {
            log.warn("WARN: User with id {} has been found in the system!", actorId);
            throw new UserNotFoundException("User with id " + actorId + " not found in the system!");
        }
    }

    @Transactional
    public MedicalRecordResponse registerMedicalRecord(Long creatorId) {

        // map request to entity
        MedicalRecordEntity entity = MedicalRecordEntity.builder()
                        .createdBy(creatorId)
                        .build();

        // save to database
        medicalRecordRepository.save(entity);

        // save to mongoDb
        medicalRecordMongoService.createNewMedicalRecordInMongoDb(entity);

        // logs:
        actionLogService.logAction(
                creatorId,
                ActionTypeFeatures.CREATE_NEW_PATIENT_MEDICAL_RECORD,
                entity.getRecordId()
        );

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

        MedicalRecordResponse result = medicalRecordRepository.findById(recordId)
                .map(medicalRecordMapper::toMedicalRecordResponse)
                .orElseThrow(() -> new RuntimeException("Medical Record not found"));

        // search for patient name and assigned user name
        Long patientId = result.getPatientId();
        Long assignedUserId = result.getAssignedUser();
        String patientName = userSnapshotService.getFullNameByExternalUserId(patientId);
        String assignedUserName = userSnapshotService.getFullNameByExternalUserId(assignedUserId);
        result.setPatientName(patientName);
        result.setAssignedUserName(assignedUserName);
        return result;
    }

    @Override
    public MedicalRecordResponse getByCode(UUID recordCode) {
        return medicalRecordRepository.findByRecordCode(recordCode)
                .map(medicalRecordMapper::toMedicalRecordResponse)
                .orElseThrow(() -> new RuntimeException("Medical Record not found"));
    }

    private void fillPatientNameAndAssignedUserName(Page<MedicalRecordResponse> records) {
        // get all patientIds and assignedUserIds to reduce number of queries
        List<Long> patientIds = records.stream()
                .map(MedicalRecordResponse::getPatientId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> assignedUserIds = records.stream()
                .map(MedicalRecordResponse::getAssignedUser)
                .distinct()
                .collect(Collectors.toList());

        // use query in clause to get all user snapshots at once
        var patientSnapshots = userSnapshotRepository.findByExternalUserIdIn(patientIds)
                .stream()
                .collect(Collectors.toMap(
                        snapshot -> snapshot.getExternalUserId(),
                        snapshot -> snapshot.getFullName()
                ));

        var assignedUserSnapshots = userSnapshotRepository.findByExternalUserIdIn(assignedUserIds)
                .stream()
                .collect(Collectors.toMap(
                        snapshot -> snapshot.getExternalUserId(),
                        snapshot -> snapshot.getFullName()
                ));

        // adjust result to add patientName and assignedUserName
        records.forEach(entity -> {
            Long patientId = entity.getPatientId();
            Long assignedUserId = entity.getAssignedUser();

            String patientName = patientSnapshots.get(patientId);
            String assignedUserName = assignedUserSnapshots.get(assignedUserId);

            entity.setPatientName(patientName);
            entity.setAssignedUserName(assignedUserName);
        });
    }

    @Override
    public Page<MedicalRecordResponse> getAll(
            Integer page, Integer size, Long viewerId
    ) {

        // validate viewer existence
        validateViewerExistence(viewerId);

        // log the view all action
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_ALL_PATIENT_MEDICAL_RECORDS, null);

        // create pagable with desc by createdAt
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<MedicalRecordEntity> medicalRecordEntities = medicalRecordRepository.findAll(pageable);
        Page<MedicalRecordResponse> result = medicalRecordMapper.toMedicalRecordResponsePage(medicalRecordEntities);

        // fill patient name and assignedUserName
        fillPatientNameAndAssignedUserName(result);

        return result;
    }

    @Override
    public Page<MedicalRecordResponse> getByPatientId(
            Long patientId, Integer page, Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MedicalRecordEntity> entities =  medicalRecordRepository.findByPatientId(patientId, pageable);

        Page<MedicalRecordResponse> result = medicalRecordMapper.toMedicalRecordResponsePage(entities);

        // fill patient name and assignedUserName
        fillPatientNameAndAssignedUserName(result);
        return result;
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
