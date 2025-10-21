package sum25.group03.monitoringservice.service;

import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.repository.RawTestResultRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RawTestResultService {

    private  final RawTestResultRepository rawTestRepo;
    public RawTestResultService(RawTestResultRepository rawTestRepo) {
        this.rawTestRepo = rawTestRepo;
    }
    public RawTestResult addRawTestResult(RawTestResult rawTestResult){
        rawTestResult.setReceivedAt(Instant.now());
        return rawTestRepo.save(rawTestResult);
    }
    public Optional<RawTestResult> findRawByTestOrderId(String testOrderId){
        return rawTestRepo.findFirstByTestOrderId(testOrderId);
    }
    public List<RawTestResult> getAllRawResults(){
        return rawTestRepo.findAllByOrderByReceivedAtDesc();
    }
}
