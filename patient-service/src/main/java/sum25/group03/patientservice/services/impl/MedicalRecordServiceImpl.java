package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.entities.MedicalRecordEntity;
import sum25.group03.patientservice.mapper.MedicalRecordMapper;
import sum25.group03.patientservice.repositories.MedicalRecordRepository;
import sum25.group03.patientservice.services.interfaces.MedicalRecordService;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

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
}
