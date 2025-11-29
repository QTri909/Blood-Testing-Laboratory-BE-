package sum25.group03.testorderservice.services.interfaces;

import jakarta.servlet.http.HttpServletResponse;

public interface ReportService {
    void exportPdf(Long testOrderId , HttpServletResponse response) throws Exception;
    void exportExcel(Long patientId, HttpServletResponse response) throws Exception;
    byte[] exportPdfToBytes(Long testOrderId) throws Exception;
}
