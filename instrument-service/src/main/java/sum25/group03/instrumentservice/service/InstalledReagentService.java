package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.request.UpdateReagentStatusRequest;
import sum25.group03.instrumentservice.controller.response.InstalledReagentPageResponse;
import sum25.group03.instrumentservice.controller.response.UpdateReagentStatusResponse;

import java.util.List;
import java.util.Map;

public interface InstalledReagentService {
    InstalledReagentPageResponse findInstalledReagentById(Long id);
    InstalledReagentPageResponse findAllInstalledReagents(String keyword, String sort, String status,
                                                          Integer instrumentId, int page, int size);
    UpdateReagentStatusResponse updateReagentStatus(UpdateReagentStatusRequest request);
    Map<Long, String> getAllReagentByInstrumentId(Long instrumentId);
    void deleteReagents(Long reagentId);
}
