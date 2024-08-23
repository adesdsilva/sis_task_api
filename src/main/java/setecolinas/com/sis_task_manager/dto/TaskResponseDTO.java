package setecolinas.com.sis_task_manager.dto;

import java.time.LocalDate;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        String status,
        Long taskListId
) {
}
