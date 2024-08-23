package setecolinas.com.sis_task_manager.repositoryTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import setecolinas.com.sis_task_manager.model.TaskList;
import setecolinas.com.sis_task_manager.repository.TaskListRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("h2")
public class TaskListRepositoryTest {

    @Autowired
    private TaskListRepository taskListRepository;

    @Test
    public void testFindTaskListsWithTasksOrderedByFavoritesAndCreation() {
        List<TaskList> taskLists = taskListRepository.findTaskListsWithTasksOrderedByFavoritesAndCreation();

        assertThat(taskLists).isNotNull();
        assertThat(taskLists).isSortedAccordingTo((tl1, tl2) -> {
            int cmp = Boolean.compare(tl2.isFavorite(), tl1.isFavorite());
            if (cmp == 0) {
                cmp = tl1.getId().compareTo(tl2.getId());
            }
            return cmp;
        });
    }

    @Test
    public void testFindAllByOrderByIsFavoriteDescCreatedDateAsc() {
        // Defina o Pageable para a consulta
        Pageable pageable = PageRequest.of(0, 5,
                Sort.by(Sort.Order.desc("isFavorite"),
                        Sort.Order.asc("createdDate")));

        // Quando
        Page<TaskList> taskListsPage = taskListRepository.findAllByOrderByIsFavoriteDescCreatedDateAsc(pageable);

        // Então
        assertThat(taskListsPage).isNotNull();
        assertThat(taskListsPage.getContent()).isSortedAccordingTo((tl1, tl2) -> {
            int cmp = Boolean.compare(tl2.isFavorite(), tl1.isFavorite());
            if (cmp == 0) {
                cmp = tl1.getCreatedDate().compareTo(tl2.getCreatedDate());
            }
            return cmp;
        });
    }
}

