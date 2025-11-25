package sum25.group03.instrumentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.instrumentservice.controller.request.UpdateReagentStatusRequest;
import sum25.group03.instrumentservice.controller.response.InstalledReagentPageResponse;
import sum25.group03.instrumentservice.controller.response.UpdateReagentStatusResponse;
import sum25.group03.instrumentservice.service.InstalledReagentService;
import sum25.group03.instrumentservice.service.InstrumentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/instruments/reagents")
@RequiredArgsConstructor
@Tag(name = "Installed Reagent Management", description = "APIs for managing installed reagents on instruments")
public class InstalledReagentController {
    private final InstalledReagentService installedReagentService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get installed reagent by ID",
            description = "Retrieves a specific installed reagent with all its details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Installed reagent found",
                            content = @Content(schema = @Schema(implementation = InstalledReagentPageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Installed reagent not found")
            }
    )
    public ResponseEntity<InstalledReagentPageResponse> getInstalledReagentById(@PathVariable Long id) {
        InstalledReagentPageResponse response = installedReagentService.findInstalledReagentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Get all installed reagents with search and pagination",
            description = "Retrieves all installed reagents with optional keyword search, status filter, instrument ID filter, sorting, and pagination. " +
                    "Search is performed across instrument name, lot reagent ID, and current volume fields.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Installed reagents retrieved successfully",
                            content = @Content(schema = @Schema(implementation = InstalledReagentPageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameters")
            }
    )
    public ResponseEntity<InstalledReagentPageResponse> getAllInstalledReagents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer instrumentId,
            @RequestParam(defaultValue = "id:asc") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        InstalledReagentPageResponse response = installedReagentService.findAllInstalledReagents(keyword, sort, status, instrumentId, page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-status")
    @Operation(
            summary = "Update reagent status",
            description = "Updates the status of an installed reagent with validation of allowed transitions. " +
                    "Tracks all status changes for audit purposes. Prevents updates to removed reagents and invalid transitions.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reagent status updated successfully",
                            content = @Content(schema = @Schema(implementation = UpdateReagentStatusResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid status transition or request"),
                    @ApiResponse(responseCode = "404", description = "Installed reagent not found"),
                    @ApiResponse(responseCode = "409", description = "Reagent is already removed or status is unchanged")
            }
    )
    public ResponseEntity<UpdateReagentStatusResponse> updateReagentStatus(
            @Valid @RequestBody UpdateReagentStatusRequest request) {
        UpdateReagentStatusResponse response = installedReagentService.updateReagentStatus(request);
        return ResponseEntity.ok(response);
    }

    //manager,admin, lab,service user
    @GetMapping("/names/{instrumentId}")
    @Operation(
            summary = "Get all reagent names by instrument ID",
            description = "Retrieves a list of all reagent names installed on a specific instrument.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reagent names retrieved successfully",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Instrument not found")
            }
    )
    public sum25.group03.common.response.ApiResponse<?> getAllReagentNamesByInstrumentId(@PathVariable Long instrumentId) {
        Map<Long, String> response = installedReagentService.getAllReagentByInstrumentId(instrumentId);
        return sum25.group03.common.response.ApiResponse.add("Reagent names retrieved successfully", response);
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "Delete installed reagents by reagent ID",
            description = "Deletes all installed reagents associated with the specified reagent ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Installed reagents deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Reagent not found")
            })
    public ResponseEntity<?> deleteInstalledReagentsByReagen( @RequestParam Long instrumentId, @RequestParam Long reagentId) {
        installedReagentService.deleteReagents(instrumentId,reagentId);
        return ResponseEntity.ok().build();
        }
    
}
