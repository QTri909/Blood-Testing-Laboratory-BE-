package sum25.group03.testorderservice.services.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.testorderservice.dtos.response.*;
import sum25.group03.testorderservice.entities.*;
import sum25.group03.testorderservice.entities.Comment;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.grpc.PatientGrpcClient;
import sum25.group03.testorderservice.grpc.GetPatientByIdResponse;
import sum25.group03.testorderservice.repositories.TestOrderRepository;
import sum25.group03.testorderservice.services.interfaces.ReportService;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TestOrderRepository testOrderRepository;
    private final PatientGrpcClient patientGrpcClient;

    //  EXPORT PDF (2 bảng: Order Info + Test Results)
    @Override
    @Transactional(readOnly = true)
    public void exportPdf(Long testOrderId, HttpServletResponse response) throws Exception {
        TestOrder order = testOrderRepository.findById(testOrderId)
                .orElseThrow(() -> new RuntimeException("Test order not found"));

        if(order.getStatus()!= TestOrderStatus.COMPLETED){
            throw new RuntimeException("Cannot export PDF for pending test order");
        }

        // 1. Lấy comment của TestOrder thôi
        String orderComments = Optional.ofNullable(order.getComments())
                .orElse(List.of())
                .stream()
                .map(Comment::getCommentText)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.joining(", "));


        //mook data patientid=1001
//        long patientId = order.getPatientId();
//        if(patientId==0){
//            patientId=1001;
//            order.setPatientId(patientId);
//        }
//        GetPatientByIdResponse patientResponse = patientGrpcClient.getPatientById(patientId);


//          Lấy thông tin bệnh nhân qua gRPC
        GetPatientByIdResponse patient = patientGrpcClient.getPatientById(order.getPatientId());

        List<TestResultResponseExportPDFDTO> resultsDTO = order.getTestResults().stream().map(r ->
                TestResultResponseExportPDFDTO.builder()
                        .id(r.getId())
                        .parameterName(r.getParameter().getName())
                        .value(r.getValue())
                        .flagStatus(r.getFlagStatus())
                        .status(r.getStatus())
                        .createdAt(r.getCreatedAt())
//                        .comments(Optional.ofNullable(r.getComments())
//                                .orElse(List.of())
//                                .stream()
//                                .map(c -> CommentResponseDTO.builder()
//                                        .id(c.getId())
//                                        .testOrderId(c.getTestOrder() != null ? c.getTestOrder().getId() : null)
//                                        .testResultId(c.getTestResult() != null ? c.getTestResult().getId() : null)
//                                        .userId(c.getUserId())
//                                        .commentText(c.getCommentText())
//                                        .createdAt(c.getCreatedAt())
//                                        .updatedAt(c.getUpdatedAt())
//                                        .status(c.getStatus())
//                                        .build())
//                                .toList())
                        .review(r.getReview() != null ? r.getReview().trim() : "")

//                                : List.of()

                        .build()
        ).toList();

        patient.getDateOfBirth();
        TestOrderResponseExportExcelDTO dto = TestOrderResponseExportExcelDTO.builder()
                .id(order.getId())
                .patientName(patient.getFullName())
                .phoneNumber(patient.getPhoneNumber())
                .gender(patient.getGender())
                .dateOfBirth(!patient.getDateOfBirth().isEmpty()
                        ? LocalDate.parse(patient.getDateOfBirth())
                        : null)
                .status(order.getStatus())
                .createdBy(order.getCreatedBy())
                .createdAt(order.getCreatedAt())
                .runBy(order.getRunBy())
                .runOn(order.getRunDate())
                .results(resultsDTO)
                .orderComments(orderComments)
                .build();


        //  JasperReports
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(dto));

        JasperReport report = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/test_order_detail.jrxml")
        );

        Map<String, Object> params = new HashMap<>();
        params.put("PATIENT_NAME", patient.getFullName());
        params.put("REPORT_DATE", LocalDate.now().toString());

        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);

        //  Xuất file
        String safeName = patient.getFullName().replaceAll("[\\\\/:*?\"<>|]+", "").trim();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = String.format("Detail - %s - %s.pdf", safeName, date);
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    @Transactional(readOnly = true)
    public byte[] exportPdfToBytes(Long testOrderId) throws Exception {

        TestOrder order = testOrderRepository.findById(testOrderId)
                .orElseThrow(() -> new RuntimeException("Test order not found"));

        if (order.getStatus() != TestOrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot export PDF for pending test order");
        }

        String orderComments = Optional.ofNullable(order.getComments())
                .orElse(List.of())
                .stream()
                .map(Comment::getCommentText)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.joining(", "));

        GetPatientByIdResponse patient =
                patientGrpcClient.getPatientById(order.getPatientId());

        List<TestResultResponseExportPDFDTO> resultsDTO =
                order.getTestResults().stream().map(r ->
                        TestResultResponseExportPDFDTO.builder()
                                .id(r.getId())
                                .parameterName(r.getParameter().getName())
                                .value(r.getValue())
                                .flagStatus(r.getFlagStatus())
                                .status(r.getStatus())
                                .createdAt(r.getCreatedAt())
//                                .comments(Optional.ofNullable(r.getComments())
//                                        .orElse(List.of())
//                                        .stream()
//                                        .map(c -> CommentResponseDTO.builder()
//                                                .id(c.getId())
//                                                .testOrderId(c.getTestOrder() != null ? c.getTestOrder().getId() : null)
//                                                .testResultId(c.getTestResult() != null ? c.getTestResult().getId() : null)
//                                                .userId(c.getUserId())
//                                                .commentText(c.getCommentText())
//                                                .createdAt(c.getCreatedAt())
//                                                .updatedAt(c.getUpdatedAt())
//                                                .status(c.getStatus())
//                                                .build())
//                                        .toList())
                                .review(r.getReview() != null ? r.getReview().trim() : "")
                                .build()
                ).toList();

        TestOrderResponseExportExcelDTO dto =
                TestOrderResponseExportExcelDTO.builder()
                        .id(order.getId())
                        .patientName(patient.getFullName())
                        .phoneNumber(patient.getPhoneNumber())
                        .gender(patient.getGender())
                        .dateOfBirth(!patient.getDateOfBirth().isEmpty()
                                ? LocalDate.parse(patient.getDateOfBirth())
                                : null)
                        .status(order.getStatus())
                        .createdBy(order.getCreatedBy())
                        .createdAt(order.getCreatedAt())
                        .runBy(order.getRunBy())
                        .runOn(order.getRunDate())
                        .results(resultsDTO)
                        .orderComments(orderComments)
                        .build();

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(List.of(dto));

        JasperReport report = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/test_order_detail.jrxml")
        );

        Map<String, Object> params = new HashMap<>();
        params.put("PATIENT_NAME", patient.getFullName());
        params.put("REPORT_DATE", LocalDate.now().toString());

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(report, params, dataSource);

        // ✅ ✅ THIS IS YOUR PDF byte[]
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            return baos.toByteArray();
        }
    }


    //  EXPORT EXCEL (Danh sách test order của 1 bệnh nhân)
    @Override
    public void exportExcel(Long patientId, HttpServletResponse response) throws Exception {
        List<TestOrder> orders ;
        if(patientId!=null){
            orders= testOrderRepository.findByPatientId(patientId);
        }
        else{
            // Nếu patientId null, lấy tất cả test orders của tháng hiện tại
            LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            orders = testOrderRepository.findByCreatedAtBetween(firstDay.atStartOfDay(), lastDay.plusDays(1).atStartOfDay());
        }

        if (orders.isEmpty()) {
            LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            orders = testOrderRepository.findByCreatedAtBetween(firstDay.atStartOfDay(), lastDay.plusDays(1).atStartOfDay());
        }

        GetPatientByIdResponse patient = patientId !=null ?patientGrpcClient.getPatientById(patientId) :null ;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test Orders");

        // Header
        Row header = sheet.createRow(0);
        String[] headers = {"Id Test Order", "Patient Name", "Gender", "Date of Birth", "Phone Number", "Status", "Created By", "Created On", "Run By", "Run On"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // Data
        int rowIdx = 1;
        for (TestOrder o : orders) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(o.getId());
            row.createCell(1).setCellValue(patient !=null ?patient.getFullName() : "");
            row.createCell(2).setCellValue(patient !=null ?patient.getGender() : "");
            row.createCell(3).setCellValue(patient !=null ? patient.getDateOfBirth() : "");
            row.createCell(4).setCellValue(patient !=null ?patient.getPhoneNumber() : "");
            row.createCell(5).setCellValue(o.getStatus().name());
            row.createCell(6).setCellValue(o.getCreatedBy() != null ? o.getCreatedBy().toString() : "");
            row.createCell(7).setCellValue(o.getCreatedAt() != null ? o.getCreatedAt().toString() : "");
            if(o.getStatus()== TestOrderStatus.COMPLETED){
                row.createCell(8).setCellValue(o.getRunBy() != null ? o.getRunBy().toString() : "");
                row.createCell(9).setCellValue(o.getRunDate() != null ? o.getRunDate().toString() : "");
            } else {
                row.createCell(8).setCellValue("");
                row.createCell(9).setCellValue("");
            }

        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        // Export
        String safeName = patient != null
                ? patient.getFullName().replaceAll("[\\\\/:*?\"<>|]+", "").trim()
                : "All_Patients";

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = String.format("Test Orders - %s - %s.xlsx", safeName, date);
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        response.getOutputStream().write(out.toByteArray());
    }
}
