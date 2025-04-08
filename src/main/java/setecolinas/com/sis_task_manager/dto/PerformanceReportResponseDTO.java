package setecolinas.com.sis_task_manager.dto;

import java.time.LocalDateTime;

public record PerformanceReportResponseDTO(
        Long reportId,
        Long userId,
        Integer completedTasks,
        Double averageCompletionTime,
        LocalDateTime reportDate,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}

