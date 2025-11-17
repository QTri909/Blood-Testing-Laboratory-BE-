package sum25.group03.testorderservice.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.testorderservice.services.interfaces.ReportService;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    //  PDF Export
    @GetMapping("/test-orders/pdf/{testOrderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void exportPdf(@PathVariable Long testOrderId,
                          HttpServletResponse response) throws Exception {
        reportService.exportPdf(testOrderId, response);
    }

    //  Excel Export
    @GetMapping("/test-orders/excel/{patientId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void exportExcel(@PathVariable Long patientId,
                            HttpServletResponse response) throws Exception {
        reportService.exportExcel(patientId, response);
    }
}
