package setecolinas.com.sis_task_manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import setecolinas.com.sis_task_manager.businessRole.TaskListRepositoryCustom;
import setecolinas.com.sis_task_manager.model.TaskList;

public interface TaskListRepository extends JpaRepository<TaskList, Long>, TaskListRepositoryCustom {

    Page<TaskList> findAllByOrderByIsFavoriteDescCreatedDateAsc(Pageable pageable);
}
