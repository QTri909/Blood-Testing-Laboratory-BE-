package sum25.group03.patientservice.services.interfaces;

import org.springframework.data.domain.Page;
import sum25.group03.patientservice.dtos.request.FilteredMedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.MedicalRecordRequest;
import sum25.group03.patientservice.dtos.request.NewRecordStatusRequest;
import sum25.group03.patientservice.dtos.request.UpdatedAssignedDoctor;
import sum25.group03.patientservice.dtos.response.MedicalRecordResponse;
import sum25.group03.patientservice.enums.MedicalRecordStatus;
import sum25.group03.patientservice.grpc.dtos.GrpcTestOrderFullFieldDTO;

import java.util.List;
import java.util.UUID;


public interface MedicalRecordService {
    MedicalRecordResponse registerMedicalRecord(Long creatorId);
    UpdatedAssignedDoctor updateAssignedDoctor(UpdatedAssignedDoctor updateInfo);
    MedicalRecordResponse getById(Long recordId, Long viewerId);
    MedicalRecordResponse getByCode(UUID recordCode);
    Page<MedicalRecordResponse> getAll(Integer page, Integer size, Long viewerId);
    Page<MedicalRecordResponse> getByPatientId(Long patientId, Integer page, Integer size);
    void deleteById(NewRecordStatusRequest newStatusRequest);
    List<GrpcTestOrderFullFieldDTO> getAllTestOrdersByMedicalRecordId(Long medicalRecordId, Long viewerId);

    MedicalRecordResponse updateMedicalRecordStatus(MedicalRecordStatus newStatus, Long recordId, Long updaterId);
    Page<MedicalRecordResponse> getByFilteredMedicalRecord(FilteredMedicalRecordRequest request, Long viewerId);
    Page<MedicalRecordResponse> getByAssignableMedicalRecord(FilteredMedicalRecordRequest request, Long viewerId);
}