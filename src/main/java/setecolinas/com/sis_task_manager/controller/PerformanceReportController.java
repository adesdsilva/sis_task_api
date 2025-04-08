package setecolinas.com.sis_task_manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.dto.PerformanceReportRequestDTO;
import setecolinas.com.sis_task_manager.dto.PerformanceReportResponseDTO;
import setecolinas.com.sis_task_manager.service.PerformanceReportService;

@RestController
@RequestMapping("/reports")
@Slf4j
public class PerformanceReportController {

    private final PerformanceReportService reportService;

    public PerformanceReportController(PerformanceReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<PerformanceReportResponseDTO> generateReport(@RequestBody PerformanceReportRequestDTO requestDTO) {
        log.info("Requisição para gerar relatório recebida");
        PerformanceReportResponseDTO reportDTO = reportService.generateReport(requestDTO);
        return ResponseEntity.ok(reportDTO);
    }

    @GetMapping("/{reportId}/pdf")
    public ResponseEntity<byte[]> exportReportAsPDF(@PathVariable Long reportId) {
        log.info("Requisição para exportar relatório em PDF recebida");
        byte[] pdfContent = reportService.exportReportAsPDF(reportId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=performance_report_" + reportId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @GetMapping("/{reportId}/excel")
    public ResponseEntity<byte[]> exportReportAsExcel(@PathVariable Long reportId) {
        log.info("Requisição para exportar relatório em Excel recebida");
        byte[] excelContent = reportService.exportReportAsExcel(reportId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=performance_report_" + reportId + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
    }
}

