package setecolinas.com.sis_task_manager.dto;

import java.time.LocalDateTime;

public record PerformanceReportRequestDTO(
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}

