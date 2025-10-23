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
import sum25.group03.instrumentservice.controller.response.ChangeInstrumentModeResponse;
import sum25.group03.instrumentservice.controller.response.InstallReagentResponse;
import sum25.group03.instrumentservice.service.InstrumentService;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
@Tag(name = "Instrument Management", description = "APIs for managing laboratory instruments")
public class InstrumentController {
    private final InstrumentService instrumentService;



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
}
