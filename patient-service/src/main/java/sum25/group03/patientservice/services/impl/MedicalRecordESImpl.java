package sum25.group03.patientservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.patientservice.repositories.elasticsearch.MedicalRecordESRepository;

@Service
@RequiredArgsConstructor
public class MedicalRecordESImpl {

    private MedicalRecordESRepository medicalRecordRepository;

}
