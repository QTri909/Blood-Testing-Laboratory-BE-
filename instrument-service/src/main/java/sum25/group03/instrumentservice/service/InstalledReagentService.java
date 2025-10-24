package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.request.UpdateReagentStatusRequest;
import sum25.group03.instrumentservice.controller.response.InstalledReagentPageResponse;
import sum25.group03.instrumentservice.controller.response.UpdateReagentStatusResponse;

public interface InstalledReagentService {
    InstalledReagentPageResponse findInstalledReagentById(Long id);
    InstalledReagentPageResponse findAllInstalledReagents(String keyword, String sort, String status,
                                                          Integer instrumentId, int page, int size);
    UpdateReagentStatusResponse updateReagentStatus(UpdateReagentStatusRequest request);
}
