package sum25.group03.testorderservice.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.dtos.request.TestOrderPdfEmailRequest;
import sum25.group03.testorderservice.services.interfaces.MailService;
import sum25.group03.testorderservice.services.interfaces.ReportService;

@RestController
@RequestMapping("/api/v1/reports")//  {{api_gateway}}/api/v1/test-orders/reports
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final MailService mailService;

    //  Export PDF
    @GetMapping("/test-orders/pdf/{testOrderId}")
    public void exportPdf(@PathVariable Long testOrderId,
                          HttpServletResponse response) {
        try {
            reportService.exportPdf(testOrderId, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new RuntimeException("Failed to export PDF: " + e.getMessage());
        }
    }

    @PostMapping("/test-orders/pdf/via-email")
    @ResponseStatus(HttpStatus.OK)
    public void exportPdfViaEmail(
            @RequestBody TestOrderPdfEmailRequest request
    ) {
        String toEmail = request.getEmail();
        String receiverName = request.getReceiverName();
        Long testOrderId = request.getTestOrderId();

        try {
            byte[] pdfAttachment = reportService.exportPdfToBytes(testOrderId);

            mailService.sendPdfAttachmentEmail(toEmail, receiverName,
                    "Your Test Order Report", pdfAttachment);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF for email: " + e.getMessage());
        }
    }


    //  Export Excel
    @GetMapping("/test-orders/excel/{patientId}")
    public void exportExcel(@PathVariable Long patientId,
                            HttpServletResponse response) {
        try {
            reportService.exportExcel(patientId, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException("Failed to export Excel: " + e.getMessage());
        }
    }

}
