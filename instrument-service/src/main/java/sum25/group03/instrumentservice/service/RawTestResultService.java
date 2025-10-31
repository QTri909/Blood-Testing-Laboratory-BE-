package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.model.RawTestResult;

import java.util.List;

public interface RawTestResultService {
    void deleteRawTestResult(Long resultId, String ipAddress, String userAgent);

    void autoDeleteOldResults(int retentionDays);

    List<RawTestResult> getOldBackedUpResults(int retentionDays);
}
