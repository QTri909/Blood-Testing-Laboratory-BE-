package sum25.group03.instrumentservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.instrumentservice.controller.request.ChangeInstrumentModeRequest;
import sum25.group03.instrumentservice.controller.request.InstallReagentRequest;
import sum25.group03.instrumentservice.controller.request.UpdateReagentStatusRequest;
import sum25.group03.instrumentservice.controller.response.*;
import sum25.group03.instrumentservice.service.InstrumentService;
import sum25.group03.instrumentservice.service.UsageService;

@RestController
@RequestMapping("/api/v1/instruments")
@RequiredArgsConstructor
@Tag(name = "Instrument Management", description = "APIs for managing laboratory instruments")
public class InstrumentController {
    private final InstrumentService instrumentService;
    private final UsageService usageService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get instrument by ID",
            description = "Retrieves a specific instrument with all its installed reagents",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Instrument found",
                            content = @Content(schema = @Schema(implementation = InstrumentResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Instrument not found")
            }
    )
    public ResponseEntity<InstrumentResponse> getInstrumentById(@PathVariable Long id) {
        InstrumentResponse response = instrumentService.findInstrumentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @Operation(
            summary = "Get all instruments with search and pagination",
            description = "Retrieves all instruments with optional keyword search, status filter, sorting, and pagination. " +
                    "Each instrument includes its installed reagents.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Instruments retrieved successfully",
                            content = @Content(schema = @Schema(implementation = InstrumentPageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameters")
            }
    )
    public ResponseEntity<InstrumentPageResponse> getAllInstruments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "id:asc") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        InstrumentPageResponse response = instrumentService.findAllInstruments(keyword, sort, status, page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-mode")
    @Operation(
            summary = "Change instrument mode/status",
            description = "Changes the operational mode of an instrument (Ready, Maintenance, Inactive) " +
                    "after validating with Warehouse Service that the instrument is active",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Instrument mode changed successfully",
                            content = @Content(schema = @Schema(implementation = ChangeInstrumentModeResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request or validation failed"),
                    @ApiResponse(responseCode = "404", description = "Instrument not found"),
                    @ApiResponse(responseCode = "409", description = "Instrument is not active in Warehouse Service"),
                    @ApiResponse(responseCode = "503", description = "Warehouse Service unavailable")
            }
    )
    public ResponseEntity<ChangeInstrumentModeResponse> changeInstrumentMode(
            @Valid @RequestBody ChangeInstrumentModeRequest request) {
        ChangeInstrumentModeResponse response = instrumentService.changeInstrumentMode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/install-reagent")
    @Operation(
            summary = "Install a reagent on an instrument",
            description = "Installs a new reagent bottle on an instrument by scanning its barcode/batch number. " +
                    "Validates the reagent with Warehouse Service before installation",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reagent installed successfully",
                            content = @Content(schema = @Schema(implementation = InstallReagentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request or validation failed"),
                    @ApiResponse(responseCode = "404", description = "Instrument or reagent not found"),
                    @ApiResponse(responseCode = "409", description = "Reagent is invalid, expired, or already in use"),
                    @ApiResponse(responseCode = "503", description = "Warehouse Service unavailable")
            }
    )
    public ResponseEntity<InstallReagentResponse> installReagent(
            @Valid @RequestBody InstallReagentRequest request) {
        InstallReagentResponse response = instrumentService.installReagent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instrument/{instrumentId}/usage-history")
    public sum25.group03.common.response.ApiResponse<?> getReagentUsageHistoryByInstrument(
            @PathVariable Long instrumentId,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "20") int size
    ) {
        return sum25.group03.common.response.ApiResponse.ok(usageService.getReagentUsageHistoryByInstrument(instrumentId, page, size));
    }
}
