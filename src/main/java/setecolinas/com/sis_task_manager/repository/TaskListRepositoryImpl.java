package setecolinas.com.sis_task_manager.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import setecolinas.com.sis_task_manager.businessRole.TaskListRepositoryCustom;
import setecolinas.com.sis_task_manager.model.TaskList;

import java.util.List;

@Repository
public class TaskListRepositoryImpl implements TaskListRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TaskList> findTaskListsWithTasksOrderedByFavoritesAndCreation() {
        String jpql = "SELECT tl FROM TaskList tl " +
                "LEFT JOIN FETCH tl.tasks t " +
                "ORDER BY tl.isFavorite DESC, tl.id ASC, t.id ASC";

        TypedQuery<TaskList> query = entityManager.createQuery(jpql, TaskList.class);
        return query.getResultList();
    }
}
