package setecolinas.com.sis_task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setecolinas.com.sis_task_manager.model.NotificationSetting;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    NotificationSetting findByUserId(Long userId);
}
