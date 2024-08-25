package setecolinas.com.sis_task_manager.repositoryTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import setecolinas.com.sis_task_manager.model.enums.Status;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("h2")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testFindTasksWithFilters() {
        Pageable pageable = PageRequest.of(0, 5);

        // Filtrar tarefas completadas e favoritas
        Status statusCompleted = Status.COMPLETED; // Supondo que você tenha um Status.COMPLETED definido
        Boolean isFavorite = true;

        Page<Task> tasksPage = taskRepository.findTasksWithFilters(statusCompleted, isFavorite, pageable);

        // Verificar se todas as tarefas são completadas e favoritas
        assertThat(tasksPage.getContent()).allMatch(task -> task.getStatus() == Status.COMPLETED);
        assertThat(tasksPage.getContent()).allMatch(task -> task.getTaskList().isFavorite());
    }
}

