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
import sum25.group03.instrumentservice.controller.request.CreateInstrumentRequest;
import sum25.group03.instrumentservice.controller.response.ChangeInstrumentModeResponse;
import sum25.group03.instrumentservice.controller.response.InstrumentResponse;
import sum25.group03.instrumentservice.service.InstrumentService;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
@Tag(name = "Instrument Management", description = "APIs for managing laboratory instruments")
public class InstrumentController {
    private final InstrumentService instrumentService;

    @PostMapping
    @Operation(
            summary = "Create a new instrument",
            description = "Creates a new laboratory instrument with the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Instrument created successfully",
                            content = @Content(schema = @Schema(implementation = InstrumentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "404", description = "Configuration not found")
            }
    )
    public ResponseEntity<InstrumentResponse> createInstrument(
            @Valid @RequestBody CreateInstrumentRequest request) {
        InstrumentResponse response = instrumentService.createInstrument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
}
