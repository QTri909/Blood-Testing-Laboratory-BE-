package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
import sum25.group03.patientservice.feign.IAMFeignClient;
import sum25.group03.patientservice.feign.dtos.FeignPatientResponseWrapper;
import sum25.group03.patientservice.mapper.PatientMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl {

    private final IAMFeignClient iamFeignClient;
    private final PatientMapper patientMapper;

    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final Integer DEFAULT_PAGE_NUMBER = 0;

    public List<PatientResponseDTO> getAllPatientsWith(Integer size, Integer page) {
        if (size == null)
            size = DEFAULT_PAGE_SIZE;
        if (page == null)
            page = DEFAULT_PAGE_NUMBER;

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
