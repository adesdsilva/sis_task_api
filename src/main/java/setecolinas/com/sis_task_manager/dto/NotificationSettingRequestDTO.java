package setecolinas.com.sis_task_manager.dto;

public record NotificationSettingRequestDTO(
        boolean emailNotifications,
        boolean pushNotifications
) {}
