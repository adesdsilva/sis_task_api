package setecolinas.com.sis_task_manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.graphql.data.GraphQlRepository;
import setecolinas.com.sis_task_manager.model.enums.Status;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.model.TaskList;

import java.time.LocalDate;
import java.util.List;

@GraphQlRepository
public interface TaskRepository extends JpaRepository<Task, Long>, QueryByExampleExecutor<Task> {

    Page<Task> findByTaskListId(Long listId, Pageable pageable);

    Page<Task> findByIsFavorite(boolean isFavorite, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE (:status IS NULL OR t.status = :status) AND (:isFavorite IS NULL OR t.taskList.isFavorite = :isFavorite)")
    Page<Task> findTasksWithFilters(@Param("status") Status status, @Param("isFavorite") Boolean isFavorite, Pageable pageable);

    // Encontrar tarefas concluídas por lista
    List<Task> findByTaskListAndStatus(TaskList taskList, Status status);

    Page<Task> findByStatusAndTaskListIsFavorite(Status status, boolean favorite, Pageable pageable);
    Page<Task> findByStatus(Status status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.assignedUser.id = :userId AND t.status = 'COMPLETED' AND t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findCompletedTasksByUserAndPeriod(@Param("userId") Long userId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

}
