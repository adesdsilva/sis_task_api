package setecolinas.com.sis_task_manager.businessRole;

import setecolinas.com.sis_task_manager.model.TaskList;

import java.util.List;

public interface TaskListRepositoryCustom {

    List<TaskList> findTaskListsWithTasksOrderedByFavoritesAndCreation();
}
