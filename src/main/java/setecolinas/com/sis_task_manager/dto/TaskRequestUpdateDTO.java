package setecolinas.com.sis_task_manager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TaskRequestUpdateDTO(
        String title,
        String description,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate dueDate
) {
}
