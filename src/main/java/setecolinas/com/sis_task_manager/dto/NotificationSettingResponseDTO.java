package setecolinas.com.sis_task_manager.dto;

public record NotificationSettingResponseDTO(
        Long id,
        Long userId,
        boolean notifyOnTaskDue,
        boolean notifyOnNewAssignment
) {}
