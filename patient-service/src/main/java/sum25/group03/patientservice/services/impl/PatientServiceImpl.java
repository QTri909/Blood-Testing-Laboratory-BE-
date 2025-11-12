package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
import sum25.group03.patientservice.dtos.response.UserSnapshotResponse;
import sum25.group03.patientservice.entities.UserSnapshotEntity;
import sum25.group03.patientservice.enums.ActionTypeFeatures;
import sum25.group03.patientservice.exception.user.snapshot.UserNotFoundException;
import sum25.group03.patientservice.feign.IAMFeignClient;
import sum25.group03.patientservice.feign.dtos.FeignPatientResponseWrapper;
import sum25.group03.patientservice.grpc.TestOrderGrpcClient;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderDTO;
import sum25.group03.patientservice.mapper.PatientMapper;
import sum25.group03.patientservice.mapper.UserSnapshotMapper;
import sum25.group03.patientservice.repositories.postgres.UserSnapshotRepository;
import sum25.group03.patientservice.services.interfaces.PatientService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final IAMFeignClient iamFeignClient;
    private final PatientMapper patientMapper;
    private final UserSnapshotMapper userSnapshotMapper;
    private final TestOrderGrpcClient testOrderGrpcClient;
    private final UserSnapshotRepository userSnapshotRepository;
    private final ActionLogService actionLogService;

    // check if a viewerId belongs to our system or not, if not, throw exception and warn to admin
    private void validateViewerExistence(Long actorId) {
        boolean isViewerExisted = userSnapshotRepository.existsByExternalUserId(actorId);
        if (!isViewerExisted) {
            log.warn("WARN: User with id {} has been found in the system!", actorId);
            throw new UserNotFoundException("User with id " + actorId + " not found in the system!");
        }
    }

    @Override
    public List<PatientResponseDTO> getAllIAMPatientsWith(Integer size, Integer page) {
        // Call IAM service to fetch patients info
        FeignPatientResponseWrapper wrapper = iamFeignClient.fetchPatientsInfo(page, size);
        if (wrapper == null || wrapper.getContent() == null)
            throw new RuntimeException("Failed to fetch patients info from IAM service");

        // debug:
        log.info("Feign patient information: {}", wrapper);

        // Map FeignPatientDTO to PatientResponseDTO
        return patientMapper.toResponseDtoList(wrapper.getContent());
    }

    @Override
    public Page<UserSnapshotResponse> getAllPatientsWith(Integer size, Integer page) {

        // find all patients by role:
        Pageable pageable = PageRequest.of(page, size);
        String role = "\"PATIENT\"";
        Page<UserSnapshotEntity> patientEntities = userSnapshotRepository
                .findByRolesContaining(role, pageable);

        // debug:
        return userSnapshotMapper.toResponsePage(patientEntities);
    }

    @Override
    public UserSnapshotResponse getPatientByExternalUserId(Long patientId, Long viewerId) {

        // logs for debug
        actionLogService.logAction(viewerId, ActionTypeFeatures.VIEW_PATIENT_INFO_BY_ID, patientId);

        // fetch from database the patient info:
        UserSnapshotEntity patientEntity = userSnapshotRepository.findByExternalUserId(patientId)
                .orElseThrow(() -> new UserNotFoundException("Patient with id " + patientId + " not found"));

        if (patientEntity.getRoles() == null || !patientEntity.getRoles().contains("PATIENT")) {
            throw new UserNotFoundException("User with id " + patientId + " is not a patient");
        }

        return userSnapshotMapper.toResponse(patientEntity);
    }

    @Override
    public GrpcTestOrderDTO getLatestByPatientId(Long patientId) {

        // debug:
        log.info("Fetching latest medical record for patientId: {}", patientId);

        // Kiểm tra bệnh nhân có tồn tại hay không
        validateViewerExistence(patientId);

        // Gọi gRPC sang test-order-service để lấy test order gần nhất
        GrpcTestOrderDTO latestTestOrder = null;
        try {
            log.info("Calling gRPC to fetch latest test order for patientId: {}", patientId);
            latestTestOrder = testOrderGrpcClient.getLatestTestOrderByPatientId(patientId);
        } catch (Exception e) {
            log.warn("Không thể lấy test order mới nhất cho patientId {}: {}", patientId, e.getMessage());
        }

        // Nếu muốn bổ sung test order vào response (phần này optional, có thể mở rộng DTO)
        return latestTestOrder;
    }

}
