package setecolinas.com.sis_task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setecolinas.com.sis_task_manager.model.SubTask;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
}
