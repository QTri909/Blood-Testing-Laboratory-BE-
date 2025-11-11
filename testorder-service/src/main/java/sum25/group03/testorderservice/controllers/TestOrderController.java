package sum25.group03.testorderservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.dtos.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dtos.response.CreationTestOrderResponse;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.dtos.request.TestOrderFiltering;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseForInstrument;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-orders")
@RequiredArgsConstructor
@Slf4j
public class TestOrderController {

    private final TestOrderService testOrderService;

    // -------- THUYEN --------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<TestOrderResponseDTO>> getAllTestOrders(
            @RequestHeader("X-User-Id") Long viewerId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return ApiResponse.add("Get all test orders successfully", testOrderService.getAllTestOrders(page, size, viewerId));
    }

    /*
    @GetMapping("/by-medical-record/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<TestOrderResponseDTO>> getAllTestOrdersByMedicalRecordId(
            @PathVariable(name = "id") Long medicalRecordId,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        return ApiResponse.add("Get all test orders by medical record id successfully",
                testOrderService.getAllTestOrdersByMedicalRecordId(medicalRecordId, viewerId));
    }
    */

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TestOrderResponseDTO> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        return ApiResponse.add("Get test order by id successfully",
                testOrderService.getTestOrderById(id, viewerId));
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<TestOrderResponseDTO>> filterTestOrders(
            @ModelAttribute TestOrderFiltering filterInfo,
            @RequestParam Long viewerId
    ) {
        return ApiResponse.add("Filter test orders successfully",
                testOrderService.filterTestOrders(filterInfo, viewerId));
    }

    // -------- HUY -----------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TestOrderResponseDTO> createTestOrder(
            @Valid @RequestBody TestOrderRequestDTO requestDTO,
            @RequestHeader("X-User-Id") Long createdBy
    ) {
        TestOrderResponseDTO response = testOrderService.createTestOrder(requestDTO, createdBy);
        return ApiResponse.add("Test order created successfully", response);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TestOrderResponseDTO> updateTestOrder(
            @PathVariable Long id,
            @Valid @RequestBody TestOrderRequestDTO requestDTO,
            @RequestHeader("X-User-Id") Long updatedBy)
    {
        TestOrderResponseDTO response = testOrderService.updateTestOrder(id, requestDTO, updatedBy);
        return ApiResponse.add("Test order updated successfully", response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteTestOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long deletedBy)
    {
        testOrderService.deleteTestOrder(id, deletedBy);
        return ApiResponse.add("Test order deleted successfully", null);
    }

    @GetMapping("/patient/{patientId}")
    public ApiResponse<List<TestOrderResponseDTO>> getTestOrdersByPatientId(
            @PathVariable Long patientId)
    {
        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByPatientId(patientId);
        return ApiResponse.add("Get test orders by patient ID successfully", response);
    }

    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<TestOrderResponseDTO>> getTestOrdersByStatus(
            @PathVariable TestOrderStatus status)
    {
        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByStatus(status);
        return ApiResponse.add("Get test orders by status successfully", response);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TestOrderResponseDTO> updateTestOrderStatus(
            @PathVariable Long id,
            @RequestParam TestOrderStatus status,
            @RequestHeader("X-User-Id") Long updatedBy)
    {
        TestOrderResponseDTO response = testOrderService.updateTestOrderStatus(id, status, updatedBy);
        return ApiResponse.add("Test order status updated successfully", response);
    }

    @GetMapping("/created-by/{createdBy}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<TestOrderResponseDTO>> getTestOrdersByCreatedBy(
            @PathVariable Long createdBy) {
        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByCreatedBy(createdBy);
        return ApiResponse.add("Get test orders by createdBy successfully", response);
    }


    @GetMapping("/by-barcode/{barcode}")
    public ApiResponse<TestOrderResponseForInstrument> getByBarcode(
            @PathVariable
            @Pattern(regexp = "^BC-\\d{6}$", message = "Barcode must be in format BC-123456")
            String barcode)
    {
        TestOrderResponseForInstrument response = testOrderService.findLatestByBarcode(barcode);
        return ApiResponse.add("Get test order by barcode successfully", response);
    }

    @PostMapping("/create-unmatched-order")
    public ApiResponse<CreationTestOrderResponse> createUnmatchedOrder(
            @Valid @RequestParam String barcode)
    {
        CreationTestOrderResponse response = testOrderService.createTestOrderForExternalSystem(barcode);
        return ApiResponse.add("Create unmatched test order successfully", response);
    }
}