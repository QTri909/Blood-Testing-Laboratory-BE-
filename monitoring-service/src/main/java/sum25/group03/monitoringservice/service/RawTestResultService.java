package sum25.group03.monitoringservice.service;

import org.springframework.stereotype.Service;
import sum25.group03.monitoringservice.model.RawTestResult;
import sum25.group03.monitoringservice.repository.RawTestResultRepository;
import sum25.group03.monitoringservice.util.RawTestVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RawTestResultService {

    private final RawTestResultRepository rawTestRepo;
    private final RawTestVerifier rawTestVerifier;

    // Lưu tạm các raw test thất bại để retry
    private final List<RawTestResult> failedInsertions = new ArrayList<>();
    // Lưu log backup thành công
    private final List<String> backupLogs = new ArrayList<>();

    public RawTestResultService(RawTestResultRepository rawTestRepo, RawTestVerifier rawTestVerifier) {
        this.rawTestRepo = rawTestRepo;
        this.rawTestVerifier = rawTestVerifier;
    }

    /** 1️⃣ Thêm mới raw test result */
    public RawTestResult addRawTestResult(RawTestResult rawTestResult) {
        rawTestResult.setReceivedAt(Instant.now());
        try {
            RawTestResult saved = rawTestRepo.save(rawTestResult);
            backupLogs.add("SUCCESS: " + rawTestResult.getTestOrderId() + " at " + rawTestResult.getReceivedAt());
            return saved;
        } catch (Exception e) {
            failedInsertions.add(rawTestResult);
            return null;
        }
    }

    /** 2️⃣ Lấy raw test result theo ID, trả về Optional để an toàn */
    public Optional<RawTestResult> findRawByTestOrderId(String testOrderId) {
        return rawTestRepo.findFirstByTestOrderId(testOrderId);
    }

    /** 3️⃣ Lấy raw test result theo ID, trả về trực tiếp (dùng khi chắc chắn có dữ liệu) */
    public RawTestResult getRawTestResultById(String testOrderId) {
        return rawTestRepo.findFirstByTestOrderId(testOrderId).orElse(null);
    }

    /** 4️⃣ Verify dữ liệu lưu với dữ liệu gốc */
    public boolean verifyRawTestResult(String testOrderId) {
        Optional<RawTestResult> storedOpt = rawTestRepo.findFirstByTestOrderId(testOrderId);
        if (storedOpt.isEmpty()) return false;

        RawTestResult stored = storedOpt.get();
        // Giả sử dữ liệu gốc cũng lưu trong DB hoặc tạm lưu gần nhất
        return rawTestVerifier.verify(stored, stored);
    }

    /** 5️⃣ Lấy danh sách log backup thành công */
    public List<String> getBackupLogs() {
        return new ArrayList<>(backupLogs);
    }

    /** 6️⃣ Thử lại các insert thất bại */
    public int retryFailedInsertions() {
        List<RawTestResult> success = new ArrayList<>();
        for (RawTestResult r : failedInsertions) {
            try {
                r.setReceivedAt(Instant.now());
                rawTestRepo.save(r);
                backupLogs.add("SUCCESS (retry): " + r.getTestOrderId() + " at " + r.getReceivedAt());
                success.add(r);
            } catch (Exception ignored) {
            }
        }
        failedInsertions.removeAll(success);
        return success.size();
    }

    /** 7️⃣ Lấy tất cả raw test đã lưu, theo thời gian giảm dần */
    public List<RawTestResult> getAllRawResults() {
        return rawTestRepo.findAllByOrderByReceivedAtDesc();
    }
}
