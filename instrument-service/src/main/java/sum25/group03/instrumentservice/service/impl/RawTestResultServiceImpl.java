package sum25.group03.instrumentservice.service.impl;

import com.google.common.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.audit.service.AuditLogService;
import sum25.group03.instrumentservice.controller.response.InstrumentPageResponse;
import sum25.group03.instrumentservice.controller.response.InstrumentResponse;
import sum25.group03.instrumentservice.controller.response.RawTestResultPageResponse;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;
import sum25.group03.instrumentservice.exception.ResourceNotFoundException;
import sum25.group03.instrumentservice.repository.RawTestResultRepository;
import sum25.group03.instrumentservice.service.RawTestResultService;
import sum25.group03.instrumentservice.model.RawTestResult;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawTestResultServiceImpl implements RawTestResultService {

    private final RawTestResultRepository rawTestResultRepository;
    private final AuditLogService auditLogService;

    @Override
    public void deleteRawTestResult(Long resultId, String ipAddress, String userAgent) {
        log.info("Attempting to delete RawTestResult with ID: {}", resultId);

        RawTestResult result = rawTestResultRepository.findById(resultId)
                .orElseThrow(() -> {
                    log.warn("Delete failed. RawTestResult not found with ID: {}", resultId);
                    return new ResourceNotFoundException("Không tìm thấy RawTestResult với ID: " + resultId);
                });

        if (!isSafeToDelete(result)) {
            log.warn("Delete failed. RawTestResult with ID: {} has not been backed up to Monitoring Service", resultId);
            throw new IllegalStateException("Không thể xóa kết quả chưa được sao lưu. Vui lòng đợi đồng bộ với Monitoring Service.");
        }

        performDelete(result, "MANUAL_DELETE", ipAddress, userAgent);
    }

    @Override
    public void autoDeleteOldResults(int retentionDays) {
        log.info("Starting auto-delete of old raw test results (retention: {} days)", retentionDays);

        try {
            List<RawTestResult> oldResults = getOldBackedUpResults(retentionDays);
            log.info("Found {} old backed-up results eligible for deletion", oldResults.size());

            int deletedCount = 0;
            int failedCount = 0;

            for (RawTestResult result : oldResults) {
                try {
                    performDelete(result, "AUTO_DELETE", "system", "auto-delete-job");
                    deletedCount++;
                } catch (Exception e) {
                    log.error("Failed to auto-delete RawTestResult with ID: {}", result.getResultId(), e);
                    failedCount++;
                }
            }

            log.info("Auto-delete completed. Deleted: {}, Failed: {}", deletedCount, failedCount);
        } catch (Exception e) {
            log.error("Error during auto-delete job: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<RawTestResult> getOldBackedUpResults(int retentionDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        log.debug("Fetching results older than: {} (retention days: {})", cutoffDate, retentionDays);
        return rawTestResultRepository.findOldBackedUpResults(cutoffDate);
    }

    @Override
    public RawTestResultPageResponse getResultFromInstrumentId(Long instrumentId, int page, int size) {
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());
        Page<RawTestResult> testResultPage = rawTestResultRepository.getByInstrumentId(instrumentId, pageable);

        List<RawTestResultResponse> testResultPageResponse = testResultPage.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        RawTestResultPageResponse response = new RawTestResultPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalPages(testResultPage.getTotalPages());
        response.setTotalElements(testResultPage.getTotalElements());
        response.setRawTestResults(testResultPageResponse);
        return response;

    }

    @Override
    public boolean deleteById(Long resultId) {
        RawTestResult result = rawTestResultRepository.findById(resultId).orElse(null);
        if(result == null ) {
            log.error("Delete failed. RawTestResult not found with ID: {}", resultId);
            throw new ResourceNotFoundException("RawTestResult not found with ID: " + resultId);
        }
        if (isSafeToDelete(result)) {
            rawTestResultRepository.deleteById(resultId);
            log.info("Successfully deleted RawTestResult with ID: {}", resultId);
            return true;
        }
        return false;
    }


    private void performDelete(RawTestResult result, String deleteType, String ipAddress, String userAgent) {
        Long resultId = result.getResultId();
        Long testOrderId = result.getTestOrderId();
        Long instrumentId = result.getInstrument() != null ? result.getInstrument().getId() : null;

        try {
            rawTestResultRepository.deleteById(resultId);

            auditLogService.logWriteSuccess(
                    deleteType,
                    "RawTestResult",
                    resultId.toString(),
                    ipAddress,
                    userAgent,
                    auditLogService.createFieldChanges(
                            "deletion_info",
                            String.format("testOrderId=%d, instrumentId=%d", testOrderId, instrumentId),
                            "DELETED"
                    )
            );

            log.info("Successfully deleted RawTestResult with ID: {} (Type: {})", resultId, deleteType);
        } catch (Exception e) {
            log.error("Failed to delete RawTestResult with ID: {}", resultId, e);
            auditLogService.logWriteFailure(
                    deleteType,
                    "RawTestResult",
                    resultId.toString(),
                    ipAddress,
                    userAgent,
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
            throw e;
        }
    }

    private boolean isSafeToDelete(RawTestResult result) {
        return (result.getIsSentToMonitoring() != null && result.getIsSentToMonitoring()) ||
                (result.getIsSynced() != null && result.getIsSynced());
    }

    public static Map<String, Double> convertJsonStringToMap(String jsonString) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Double>>() {}.getType();
        return gson.fromJson(jsonString, mapType);
    }

    private RawTestResultResponse mapToResponse(RawTestResult rawTestResult) {
        return RawTestResultResponse.builder()
                .resultId(rawTestResult.getResultId())
                .testOrderId(rawTestResult.getTestOrderId())
                .instrumentId(rawTestResult.getInstrument() != null ? rawTestResult.getInstrument().getId() : null)
                .rawData(convertJsonStringToMap(rawTestResult.getRawData()))
                .hl7Message(rawTestResult.getHl7Message())
                .isSentToMonitoring(rawTestResult.getIsSentToMonitoring())
                .isSynced(rawTestResult.getIsSynced())
                .createdAt(rawTestResult.getCreatedAt())
                .build();
    }

}
