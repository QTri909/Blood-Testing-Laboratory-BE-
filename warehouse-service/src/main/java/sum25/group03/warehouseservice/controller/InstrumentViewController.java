package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.response.InstrumentPageResponse;
import sum25.group03.warehouseservice.dto.response.InstrumentResponse;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.service.instumentview.InstrumentViewService;

@RestController
@RequestMapping("/api/v1/instruments")
@RequiredArgsConstructor
public class InstrumentViewController {

    private final InstrumentViewService instrumentViewService;

    private InstrumentPageResponse buildPageResponse(Page<InstrumentResponse> page) {
        return InstrumentPageResponse.builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .message(page.isEmpty() ? "No Data" : "Success")
                .build();
    }

    @GetMapping
    public ResponseEntity<InstrumentPageResponse> getAllInstruments(
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(buildPageResponse(instrumentViewService.getAllInstruments(pageable)));
    }

    @GetMapping("/search")
    public ResponseEntity<InstrumentPageResponse> searchInstruments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String status,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(buildPageResponse(
                instrumentViewService.searchInstruments(name, model, status, pageable)));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<InstrumentStatusResponse> getInstrumentStatus(@PathVariable Long id) {
        InstrumentStatusResponse response = instrumentViewService.checkInstrumentStatus(id);
        return ResponseEntity.ok(response);
    }
}
