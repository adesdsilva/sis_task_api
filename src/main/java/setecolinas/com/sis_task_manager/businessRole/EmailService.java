package setecolinas.com.sis_task_manager.businessRole;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}

