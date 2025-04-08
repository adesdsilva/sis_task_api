package setecolinas.com.sis_task_manager.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.businessRole.EmailService;
import setecolinas.com.sis_task_manager.config.ResourceNotFoundException;
import setecolinas.com.sis_task_manager.dto.NotificationSettingRequestDTO;
import setecolinas.com.sis_task_manager.dto.NotificationSettingResponseDTO;
import setecolinas.com.sis_task_manager.dto.TaskResponseDTO;
import setecolinas.com.sis_task_manager.dto.UserResponseDTO;

import lombok.extern.slf4j.Slf4j;
import setecolinas.com.sis_task_manager.model.NotificationSetting;
import setecolinas.com.sis_task_manager.repository.NotificationSettingRepository;
import setecolinas.com.sis_task_manager.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    private final TaskService taskService;
    private final UserService userService;
    private final EmailService emailService;
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    public NotificationService(TaskService taskService, UserService userService,
                               EmailService emailService,
                               NotificationSettingRepository notificationSettingRepository, UserRepository userRepository) {
        this.taskService = taskService;
        this.userService = userService;
        this.emailService = emailService;
        this.notificationSettingRepository = notificationSettingRepository;
        this.userRepository = userRepository;
    }

    // Método para enviar notificação quando uma tarefa é atribuída a um usuário
    @Transactional
    public void sendTaskAssignedNotification(Long taskId) {
        log.info("Sending task assigned notification for task ID {}", taskId);
        final var findTask = taskService.findTaskById(taskId);

        if (findTask != null && findTask.getAssignedUser().getEmail() != null) {
            String subject = "New Task Assigned: " + findTask.getTitle();
            String message = "Hello " + findTask.getAssignedUser().getName() + ",\n\n" +
                    "You have been assigned a new task:\n" +
                    "Title: " + findTask.getTitle() + "\n" +
                    "Description: " + findTask.getDescription() + "\n" +
                    "Due Date: " + findTask.getDueDate() + "\n\n" +
                    "Please log in to your account to view and manage the task.";

            emailService.sendEmail(findTask.getAssignedUser().getEmail(), subject, message);
            log.info("Task assigned notification sent to user ID {}", findTask.getAssignedUser().getId());
        } else {
            log.warn("No assigned user or email found for task ID {}", taskId);
        }
    }

    // Método para enviar notificações diárias sobre tarefas com vencimento próximo
    @Scheduled(cron = "0 0 8 * * ?") // Executa todos os dias às 08:00 da manhã
    public void sendDueDateNotifications() {
        log.info("Starting process to send due date notifications for tasks due soon");

        List<TaskResponseDTO> tasksDueSoon = taskService.getAllTasksDueSoon();

        tasksDueSoon.forEach(this::notifyUserForTaskDueSoon);

        log.info("Completed process to send due date notifications");
    }

    private void notifyUserForTaskDueSoon(TaskResponseDTO task) {
        UserResponseDTO assignedUser = userService.findAssignedUserForTask(task.id());

        if (assignedUser != null && assignedUser.email() != null) {
            String subject = createDueSoonEmailSubject(task);
            String message = createDueSoonEmailMessage(task, assignedUser);

            emailService.sendEmail(assignedUser.email(), subject, message);
            log.info("Due date notification sent to user ID {}", assignedUser);
        } log.warn("No assigned user or email found for task ID {}", task.id());
    }

    private String createDueSoonEmailSubject(TaskResponseDTO task) {
        return "Task Due Soon: " + task.title();
    }

    private String createDueSoonEmailMessage(TaskResponseDTO task, UserResponseDTO user) {
        return String.format("Hello %s,\n\n" +
                        "This is a reminder that your task is due soon:\n" +
                        "Title: %s\n" +
                        "Due Date: %s\n\n" +
                        "Please log in to your account to complete the task on time.",
                user.name(), task.title(), task.dueDate());
    }

    // Métodos adicionais para gerenciar configurações de notificação
    public NotificationSettingResponseDTO getNotificationSettings(Long userId) {
        log.info("Fetching notification settings for user ID {}", userId);
        // Recupera configurações de notificação do repositório
        NotificationSetting notificationSetting = notificationSettingRepository.findByUserId(userId);
        if (notificationSetting.getId() == null) {
            throw new ResourceNotFoundException("Notification settings not found for user ID: " + userId);
        }
        return convertToDTO(notificationSetting);
    }

    public void updateNotificationSettings(Long userId, NotificationSettingRequestDTO settingsDTO) {
        log.info("Updating notification settings for user ID {}", userId);
        // Recupera e atualiza configurações de notificação do repositório
        NotificationSetting notificationSetting = notificationSettingRepository.findByUserId(userId);
        if (notificationSetting.getId() == null) {
            throw new ResourceNotFoundException("Notification settings not found for user ID: " + userId);
        }
        // Atualize as configurações conforme o DTO
        notificationSetting.setEmailNotifications(settingsDTO.emailNotifications());
        notificationSetting.setPushNotifications(settingsDTO.pushNotifications());

        notificationSettingRepository.save(notificationSetting);
        log.info("Notification settings updated for user ID {}", userId);
    }

    public List<NotificationSettingResponseDTO> getUserNotifications(Long userId) {
        log.info("Fetching notifications for user ID {}", userId);
        // Recupera as notificações do repositório
        List<NotificationSetting> notificationSettings =
                (List<NotificationSetting>) notificationSettingRepository.findByUserId(userId);
        return notificationSettings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void sendNotification(Long userId, String message) {
        log.info("Sending notification to user ID {}: {}", userId, message);

        // Recupera o e-mail do usuário
        final var findUser = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for user ID: " + userId));
        final var userResponseDTO = new UserResponseDTO(findUser.getName(), findUser.getEmail(),
                this.userService.getToken(findUser));

        if (userResponseDTO != null && userResponseDTO.email() != null) {
            String subject = "Notification for User ID " + userId;
            emailService.sendEmail(userResponseDTO.email(), subject, message);
            log.info("Notification sent to user ID {}", userId);
        } else {
            log.warn("No email found for user ID {}", userId);
        }
    }

    private NotificationSettingResponseDTO convertToDTO(NotificationSetting notificationSetting) {
        return new NotificationSettingResponseDTO(
                notificationSetting.getId(),
                notificationSetting.getUser().getId(),
                notificationSetting.isEmailNotifications(),
                notificationSetting.isPushNotifications()
        );
    }
}
