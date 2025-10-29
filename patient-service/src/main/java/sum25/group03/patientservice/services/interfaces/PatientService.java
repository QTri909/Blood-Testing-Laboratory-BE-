package sum25.group03.patientservice.services.interfaces;

import sum25.group03.patientservice.dtos.response.PatientResponseDTO;

import java.util.List;

public interface PatientService {
    List<PatientResponseDTO> getAllPatientsWith(Integer size, Integer page);
}
