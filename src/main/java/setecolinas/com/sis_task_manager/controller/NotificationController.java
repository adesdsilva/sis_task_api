package setecolinas.com.sis_task_manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.dto.NotificationSettingRequestDTO;
import setecolinas.com.sis_task_manager.dto.NotificationSettingResponseDTO;
import setecolinas.com.sis_task_manager.service.NotificationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/settings/{userId}")
    public ResponseEntity<NotificationSettingResponseDTO> getNotificationSettings(@PathVariable Long userId) {
        log.info("Fetching notification settings for user ID {}", userId);
        NotificationSettingResponseDTO settings = notificationService.getNotificationSettings(userId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/settings/{userId}")
    public ResponseEntity<Void> updateNotificationSettings(@PathVariable Long userId,
                                                           @RequestBody NotificationSettingRequestDTO settingsDTO) {
        log.info("Updating notification settings for user ID {}", userId);
        notificationService.updateNotificationSettings(userId, settingsDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationSettingResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        log.info("Fetching notifications for user ID {}", userId);
        List<NotificationSettingResponseDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity<Void> sendNotification(@PathVariable Long userId, @RequestBody String message) {
        log.info("Sending notification to user ID {}: {}", userId, message);
        notificationService.sendNotification(userId, message);
        return ResponseEntity.noContent().build();
    }
}
