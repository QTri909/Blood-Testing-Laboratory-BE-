package sum25.group03.patientservice.services.impl;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.entities.MedicalRecordEntity;
import sum25.group03.patientservice.exception.medical.record.MedicalRecordNotFound;
import sum25.group03.patientservice.exception.user.snapshot.UserNotFoundException;
import sum25.group03.patientservice.mapper.MedicalRecordMapper;
import sum25.group03.patientservice.repositories.MedicalRecordRepository;
import sum25.group03.patientservice.repositories.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final UserSnapshotRepository userSnapshotRepository;

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

        return medicalRecordMapper.toMedicalRecordResponse(entity);
    }

    @Override
    @Transactional
    public UpdatedAssignedDoctor updateAssignedDoctor(UpdatedAssignedDoctor updateInfo) {
        Long recordId = updateInfo.getRecordId();
        Long assignedUserId = updateInfo.getAssignedUserId();
        Long updatedById = updateInfo.getUpdatedBy();

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

        // update information:
        entity.setAssignedUser(assignedUserId);
        entity.setUpdatedBy(updatedById);

        // logs the update action for auditing purposes
        // TODO: integrate with proper logging framework or auditing service to cloud watch AWS

        return updateInfo;
    }
}
