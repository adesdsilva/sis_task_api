package setecolinas.com.sis_task_manager.graphqlTest_Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.model.TaskList;
import setecolinas.com.sis_task_manager.model.enums.Status;
import setecolinas.com.sis_task_manager.repository.TaskListRepository;
import setecolinas.com.sis_task_manager.repository.TaskRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
public class TaskGraphQlIntTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskListRepository taskListRepository;

//    @BeforeEach
//    void setUp() {
//        taskRepository.deleteAll();
//        taskListRepository.deleteAll();
//
//        // Criar e salvar a lista de tarefas
//        TaskList taskList = new TaskList();
//        taskList.setTitle("Default Task List");
//        taskList.setFavorite(false); // Adicionado para garantir valores consistentes
//        taskListRepository.save(taskList);
//
//        // Criar e salvar a tarefa 1
//        Task task1 = new Task();
//        task1.setTitle("Task 1");
//        task1.setDescription("Description 1"); // Garantir que o campo description é preenchido
//        task1.setStatus(Status.PENDING);
//        task1.setTaskList(taskList);
//        task1.setDueDate(LocalDate.now().plusDays(1));
//        task1.setFavorite(false);
//        taskRepository.save(task1);
//
//        // Criar e salvar a tarefa 2 (exemplo adicional)
//        Task task2 = new Task();
//        task2.setTitle("Task 2");
//        task2.setDescription("Description 2"); // Garantir valor para description
//        task2.setStatus(Status.COMPLETED);
//        task2.setTaskList(taskList);
//        task2.setDueDate(LocalDate.now().plusDays(3));
//        task2.setFavorite(true);
//        taskRepository.save(task2);
//    }


    @Test
    void testFindAllTasks() {
        String query = """
            query {
                tasks {
                    id
                    title
                    description
                    dueDate
                    status
                    isFavorite
                }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("data.tasks")
                .entityList(Task.class)
                .hasSize(3);
    }

    @Test
    void testFindTaskById() {
        Task savedTask = taskRepository.findAll().get(0);

        String query = """
            query($id: ID!) {
                task(id: $id) {
                    id
                    title
                    description
                    dueDate
                    status
                }
            }
        """;

        graphQlTester.document(query)
                .variable("id", savedTask.getId().toString())
                .execute()
                .path("data.task")
                .entity(Task.class)
                .satisfies(task -> {
                    assert task.getId().equals(savedTask.getId());
                    assert task.getTitle().equals(savedTask.getTitle());
                });
    }

    @Test
    void testCreateTask() {
        String mutation = """
            mutation($input: TaskInput!) {
                createTask(input: $input) {
                    id
                    title
                    description
                }
            }
        """;

        Task input = new Task();
        input.setTitle("New Task");
        input.setDescription("New Description");
        input.setStatus(Status.PENDING);
        input.setFavorite(false);
        input.setDueDate(LocalDate.now());

        graphQlTester.document(mutation)
                .variable("input", input)
                .execute()
                .path("data.createTask")
                .entity(Task.class)
                .satisfies(task -> {
                    assert task.getTitle().equals("New Task");
                    assert task.getDescription().equals("New Description");
                });
    }

    @Test
    void testUpdateTask() {
        Task savedTask = taskRepository.findAll().get(0);

        String mutation = """
            mutation($id: ID!, $input: TaskInput!) {
                updateTask(id: $id, input: $input) {
                    id
                    title
                    description
                }
            }
        """;

        Task input = new Task();
        input.setTitle("Updated Task");
        input.setDescription("Updated Description");
        input.setStatus(Status.COMPLETED);
        input.setFavorite(true);
        input.setDueDate(LocalDate.now());

        graphQlTester.document(mutation)
                .variable("id", savedTask.getId().toString())
                .variable("input", input)
                .execute()
                .path("data.updateTask")
                .entity(Task.class)
                .satisfies(task -> {
                    assert task.getTitle().equals("Updated Task");
                    assert task.getDescription().equals("Updated Description");
                });
    }

    @Test
    void testDeleteTask() {
        taskRepository.deleteAll();
        taskListRepository.deleteAll();

        // Preparação: Adicionar uma tarefa no banco
        TaskList taskList = new TaskList();
        taskList.setTitle("Default Task List");
        taskList.setFavorite(false); // Adicionado para garantir valores consistentes
        taskListRepository.save(taskList);

        Task savedTask = new Task();
        savedTask.setTitle("Task 1");
        savedTask.setDescription("Description 1"); // Garantir que o campo description é preenchido
        savedTask.setStatus(Status.PENDING);
        savedTask.setTaskList(taskList);
        savedTask.setDueDate(LocalDate.now().plusDays(1));
        savedTask.setFavorite(false);
        taskRepository.save(savedTask);

        // Definição da mutação GraphQL
        String mutation = """
        mutation($id: ID!) {
            deleteTask(id: $id)
        }
    """;

        // Executar a mutação GraphQL
        graphQlTester.document(mutation)
                .variable("id", savedTask.getId().toString())
                .execute()
                .path("data.deleteTask");

        // Verificar se o método deleteById foi chamado corretamente
        assertThat(taskRepository.findById(savedTask.getId())).isEmpty();
    }
}

