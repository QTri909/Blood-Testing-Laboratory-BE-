package sum25.group03.instrumentservice.service;

import sum25.group03.instrumentservice.controller.response.PageRes;
import sum25.group03.instrumentservice.controller.response.ReagentHistoryUsageOfInstrumentRes;

public interface UsageService {
    PageRes<ReagentHistoryUsageOfInstrumentRes> getReagentUsageHistoryByInstrument(Long instrumentId, int page, int size);
}
