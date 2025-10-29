package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
import sum25.group03.patientservice.feign.IAMFeignClient;
import sum25.group03.patientservice.feign.dtos.FeignPatientResponseWrapper;
import sum25.group03.patientservice.mapper.PatientMapper;
import sum25.group03.patientservice.services.interfaces.PatientService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final IAMFeignClient iamFeignClient;
    private final PatientMapper patientMapper;

    @Override
    public List<PatientResponseDTO> getAllPatientsWith(Integer size, Integer page) {
        // Call IAM service to fetch patients info
        FeignPatientResponseWrapper wrapper = iamFeignClient.fetchPatientsInfo(page, size);
        if (wrapper == null || wrapper.getContent() == null)
            throw new RuntimeException("Failed to fetch patients info from IAM service");

        // debug:
        log.info("Feign patient information: {}", wrapper);

        // Map FeignPatientDTO to PatientResponseDTO
        return patientMapper.toResponseDtoList(wrapper.getContent());
    }
}
