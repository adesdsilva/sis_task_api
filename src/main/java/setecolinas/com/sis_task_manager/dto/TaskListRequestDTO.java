package setecolinas.com.sis_task_manager.dto;

import java.util.List;

public record TaskListRequestDTO(
        String title,
        List<TaskRequestDTO> tasks
) {
}
