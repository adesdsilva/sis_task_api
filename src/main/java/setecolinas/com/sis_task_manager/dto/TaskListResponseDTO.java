package setecolinas.com.sis_task_manager.dto;

import java.util.List;

public record TaskListResponseDTO(
        Long id,
        String title,
        List<TaskResponseDTO> tasks,
        boolean isFavorite
) {
}
