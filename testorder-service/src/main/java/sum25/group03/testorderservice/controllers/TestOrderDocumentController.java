package sum25.group03.testorderservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.entities.mongodb.TestOrderDocument;
import sum25.group03.testorderservice.services.interfaces.TestOrderDocumentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/testorder-documents")
public class TestOrderDocumentController {

    private final TestOrderDocumentService testOrderDocumentService;

    @GetMapping("{testOrderId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TestOrderDocument> getTestOrderDocumentByTestOrderId(
            @PathVariable(name = "testOrderId", required = true) Long testOrderId
    ) {
        TestOrderDocument testOrderDocument = testOrderDocumentService.getTestOrderDocumentByTestOrderId(testOrderId);
        if (testOrderDocument == null) {
            return ApiResponse.badRequest("Test order document not found for test order id: " + testOrderId, null);
        }
        return ApiResponse.add("Get test order document by test order id successfully", testOrderDocument);
    }
}
