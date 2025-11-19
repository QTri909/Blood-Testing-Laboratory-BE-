package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.audit.annotation.SkipAuditLog;
import sum25.group03.warehouseservice.dto.response.InternalInstrumentStatusResponse;
import sum25.group03.warehouseservice.service.instumentview.InstrumentViewService;

@RestController
@RequestMapping("/api/v1/instruments")
@RequiredArgsConstructor
public class InstrumentViewController {

    private final InstrumentViewService instrumentViewService;

    @SkipAuditLog
    @GetMapping("/{id}/status")
    public ApiResponse<InternalInstrumentStatusResponse> getInternalInstrumentStatus(@PathVariable Long id) {
        InternalInstrumentStatusResponse response = instrumentViewService.checkInstrumentStatus(id);
        return ApiResponse.ok(response);
    }
}
