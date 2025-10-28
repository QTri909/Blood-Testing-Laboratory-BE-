package sum25.group03.patientservice.mapper;

import org.mapstruct.Mapper;
import sum25.group03.patientservice.dtos.response.PatientResponseDTO;
import sum25.group03.patientservice.feign.dtos.FeignPatientDTO;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponseDTO toResponseDto(FeignPatientDTO feignPatientDTO);
}
