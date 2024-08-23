package setecolinas.com.sis_task_manager.dto;

import jakarta.validation.constraints.NotBlank;

public record SubTaskRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String name,
        Long taskId
) {
}
