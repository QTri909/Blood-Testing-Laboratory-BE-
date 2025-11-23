package sum25.group03.testorderservice.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.services.interfaces.ReportService;

@RestController
@RequestMapping("/api/v1/reports")//  {{api_gateway}}/api/v1/test-orders/reports
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

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
