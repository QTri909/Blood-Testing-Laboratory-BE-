package sum25.group03.iamservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import sum25.group03.iamservice.dto.response.AuditLogReponse;
import sum25.group03.iamservice.entity.AuditLog;
import sum25.group03.iamservice.repository.AuditLogRepository;
import sum25.group03.iamservice.service.Interface.AuditLogService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void record(String action, String entityName, Long entityId, String performedBy, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
        auditLogRepository.save(log);
    }

    @Override
    public List<AuditLogReponse> getAuditLogs(String entityName, Long entityId) {
        List<AuditLog> logs = auditLogRepository.findAll();

        return logs.stream().map(log -> AuditLogReponse.builder()
                .action(log.getAction())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .performedBy(log.getPerformedBy())
                .timestamp(log.getTimestamp())
                .details(log.getDetails())
                .build()).toList();
    }

    @Override
    public ByteArrayInputStream exportLogsAsExcel(String entityName, Long entityId) throws IOException {
        List<AuditLog> logs;

        // Nếu truyền entityName và entityId thì lọc theo điều kiện, ngược lại lấy tất cả
        if (entityName != null && entityId != null) {
            logs = auditLogRepository.findAll().stream()
                    .filter(l -> entityName.equals(l.getEntityName()) && entityId.equals(l.getEntityId()))
                    .toList();
        } else {
            logs = auditLogRepository.findAll();
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Audit Logs");

            // ===== Header =====
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Action", "Entity Name", "Entity ID", "Performed By", "Timestamp", "Details"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // ===== Data Rows =====
            int rowIdx = 1;
            for (AuditLog log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getId());
                row.createCell(1).setCellValue(
                        log.getAction() != null ? log.getAction() : ""
                );
                row.createCell(2).setCellValue(
                        log.getEntityName() != null ? log.getEntityName() : ""
                );
                row.createCell(3).setCellValue(
                        log.getEntityId() != null ? log.getEntityId() : 0
                );
                row.createCell(4).setCellValue(
                        log.getPerformedBy() != null ? log.getPerformedBy() : ""
                );
                row.createCell(5).setCellValue(
                        log.getTimestamp() != null ? log.getTimestamp().toString() : ""
                );
                row.createCell(6).setCellValue(
                        log.getDetails() != null ? log.getDetails() : ""
                );
            }

            // ===== Tự động co giãn cột cho đẹp =====
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }



}
