package setecolinas.com.sis_task_manager.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import setecolinas.com.sis_task_manager.controller.TaskListController;
import setecolinas.com.sis_task_manager.dto.TaskListRequestDTO;
import setecolinas.com.sis_task_manager.dto.TaskListResponseDTO;
import setecolinas.com.sis_task_manager.dto.TaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.TaskResponseDTO;
import setecolinas.com.sis_task_manager.model.Status;
import setecolinas.com.sis_task_manager.service.TaskListService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class TaskListControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskListService taskListService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TaskListController(taskListService)).build();
        MockitoAnnotations.openMocks(this); // Inicializa mocks
    }

    @Test
    public void testCreateTaskList_Success() throws Exception {
        TaskListRequestDTO requestDTO = new TaskListRequestDTO("Lista Teste", List.of(
                new TaskRequestDTO("Tarefa 1", "Descrição 1", LocalDate.now().plusDays(1))
        ));

        TaskListResponseDTO responseDTO = new TaskListResponseDTO(
                1L, // ID da lista de tarefas
                "Lista Teste", // Título da lista de tarefas
                List.of( // Lista de tarefas
                        new TaskResponseDTO(
                                1L, // ID da tarefa
                                "Tarefa 1", // Título da tarefa
                                "Descrição 1", // Descrição da tarefa
                                LocalDate.now().plusDays(1), // Data de vencimento
                                Status.PENDING.name(), // Status da tarefa
                                1L // ID da lista de tarefas
                        )
                ),
                true // isFavorite (adicione aqui o valor booleano esperado)
        );

        when(taskListService.createTaskList(requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Lista Teste"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tasks[0].title").value("Tarefa 1"));
    }
}