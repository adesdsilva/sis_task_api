package setecolinas.com.sis_task_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import setecolinas.com.sis_task_manager.dto.PerformanceReportRequestDTO;
import setecolinas.com.sis_task_manager.dto.PerformanceReportResponseDTO;
import setecolinas.com.sis_task_manager.model.PerformanceReport;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.model.User;
import setecolinas.com.sis_task_manager.repository.PerformanceReportRepository;
import setecolinas.com.sis_task_manager.repository.TaskRepository;
import setecolinas.com.sis_task_manager.repository.UserRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class PerformanceReportService {

    private final PerformanceReportRepository reportRepository;
    private final TaskRepository taskRepository; // Repositório de tarefas para calcular o desempenho
    private final UserRepository userRepository;

    public PerformanceReportService(PerformanceReportRepository reportRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public PerformanceReportResponseDTO generateReport(PerformanceReportRequestDTO requestDTO) {
        log.info("Iniciando a geração de relatório para o usuário com ID: {}", requestDTO.userId());

        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        log.info("Buscando tarefas completadas para o usuário no período de {} até {}", requestDTO.startDate(), requestDTO.endDate());

        // Buscar tarefas completadas no período
        List<Task> completedTasks = taskRepository.findCompletedTasksByUserAndPeriod(
                user.getId(), requestDTO.startDate().toLocalDate(), requestDTO.endDate().toLocalDate()
        );

        int totalCompletedTasks = completedTasks.size();
        log.info("Total de tarefas completadas: {}", totalCompletedTasks);

        // Calcular o tempo médio de conclusão usando a dueDate como referência
        double averageCompletionTime = completedTasks.stream()
                .mapToDouble(task -> ChronoUnit.MINUTES.between(requestDTO.startDate(), task.getDueDate().atStartOfDay()))
                .average()
                .orElse(0.0);

        log.info("Tempo médio para completar as tarefas: {} minutos", averageCompletionTime);

        PerformanceReport report = new PerformanceReport();
        report.setUser(user);
        report.setCompletedTasks(totalCompletedTasks);
        report.setAverageCompletionTime(averageCompletionTime);
        report.setStartDate(requestDTO.startDate());
        report.setEndDate(requestDTO.endDate());
        report.setReportDate(LocalDateTime.now());

        reportRepository.save(report);
        log.info("Relatório salvo com sucesso!");

        return new PerformanceReportResponseDTO(
                report.getId(),
                user.getId(),
                totalCompletedTasks,
                averageCompletionTime,
                report.getReportDate(),
                report.getStartDate(),
                report.getEndDate()
        );
    }

    public byte[] exportReportAsPDF(Long reportId) {
        PerformanceReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Cabeçalho
        document.add(new Paragraph("Empresa"));
        document.add(new Paragraph("Endereço, Cidade, Estado e CEP"));
        document.add(new Paragraph("Telefone: telefone Fax: fax"));

        document.add(new Paragraph("RELATÓRIO DE DESEMPENHO"));
        document.add(new Paragraph("Data: " + report.getReportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("Usuário: " + report.getUser().getName()));

        // Tabela de Tarefas
        document.add(new Paragraph("\nVISÃO GERAL DAS TAREFAS COMPLETADAS"));

        float[] columnWidths = {1, 3, 3};
        Table table = new Table(columnWidths);

        table.addCell("Tarefa");
        table.addCell("Data de Entrega");
        table.addCell("Concluída");

        // Exemplo de preenchimento da tabela
        for (Task task : taskRepository.findCompletedTasksByUserAndPeriod(report.getUser().getId(), report.getStartDate().toLocalDate(), report.getEndDate().toLocalDate())) {
            table.addCell(task.getTitle());
            table.addCell(task.getDueDate().toString());
            table.addCell("Sim");
        }

        document.add(table);

        // Resumo final
        document.add(new Paragraph("\nTarefas completadas: " + report.getCompletedTasks()));
        document.add(new Paragraph("Tempo médio para completar tarefas: " + report.getAverageCompletionTime() + " minutos"));

        document.close();

        return out.toByteArray();
    }

    public byte[] exportReportAsExcel(Long reportId) {
        PerformanceReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório de Desempenho");

            // Cabeçalhos
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            String[] headers = {"Tarefa", "Data de Entrega", "Concluída"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Preenchendo as linhas com as tarefas
            int rowNum = 1;
            for (Task task : taskRepository.findCompletedTasksByUserAndPeriod(report.getUser().getId(), report.getStartDate().toLocalDate(), report.getEndDate().toLocalDate())) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(task.getTitle());
                row.createCell(1).setCellValue(task.getDueDate().toString());
                row.createCell(2).setCellValue("Sim");
            }

            // Resumo final
            Row summaryRow = sheet.createRow(rowNum + 2);
            summaryRow.createCell(0).setCellValue("Tarefas completadas:");
            summaryRow.createCell(1).setCellValue(report.getCompletedTasks());

            Row averageRow = sheet.createRow(rowNum + 3);
            averageRow.createCell(0).setCellValue("Tempo médio para completar tarefas (minutos):");
            averageRow.createCell(1).setCellValue(report.getAverageCompletionTime());

            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório Excel", e);
        }

        return out.toByteArray();
    }

}

