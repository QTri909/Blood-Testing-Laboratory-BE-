package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.audit.annotation.SkipAuditLog;
import sum25.group03.warehouseservice.dto.response.InstrumentPageResponse;
import sum25.group03.warehouseservice.dto.response.InstrumentResponse;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
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
