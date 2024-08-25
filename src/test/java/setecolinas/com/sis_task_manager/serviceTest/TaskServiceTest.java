package setecolinas.com.sis_task_manager.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import setecolinas.com.sis_task_manager.config.ResourceNotFoundException;
import setecolinas.com.sis_task_manager.dto.*;
import setecolinas.com.sis_task_manager.model.enums.Status;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.model.TaskList;
import setecolinas.com.sis_task_manager.repository.TaskListRepository;
import setecolinas.com.sis_task_manager.repository.TaskRepository;
import setecolinas.com.sis_task_manager.service.TaskListService;
import setecolinas.com.sis_task_manager.service.TaskService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskService taskService;

    @InjectMocks
    private TaskListService taskListService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Teste de sucesso para criação de tarefa
    @Test
    public void testCreateTask_Success() {
        Long taskListId = 1L;
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Titulo da tarefa",
                "Descrição",
                LocalDate.now().plusDays(1));

        TaskList taskList = new TaskList();
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        TaskResponseDTO responseDTO = taskService.createTask(taskListId, requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Titulo da tarefa", responseDTO.title());
    }

    // Teste de falha para criação de tarefa quando a lista não é encontrada
    @Test
    public void testCreateTask_TaskListNotFound() {
        Long taskListId = 1L;
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Titulo da tarefa",
                "Descrição",
                LocalDate.now().plusDays(1));

        when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskListId, requestDTO));
    }

    // Teste de falha para criação de tarefa com título inválido
    @Test
    public void testCreateTask_InvalidTitle() {
        Long taskListId = 1L;
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Tit",
                "Descrição",
                LocalDate.now().plusDays(1));

        TaskList taskList = new TaskList();
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskListId, requestDTO));
    }

    // Teste de falha para criação de tarefa com descrição vazia
    @Test
    public void testCreateTask_EmptyDescription() {
        Long taskListId = 1L;
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Titulo da tarefa",
                "",
                LocalDate.now().plusDays(1));

        TaskList taskList = new TaskList();
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskListId, requestDTO));
    }

    // Teste de falha para criação de tarefa com data passada
    @Test
    public void testCreateTask_PastDueDate() {
        Long taskListId = 1L;
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Titulo da tarefa",
                "Descrição",
                LocalDate.now().minusDays(1));

        TaskList taskList = new TaskList();
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskListId, requestDTO));
    }

    // Teste de sucesso para atualização de tarefa
    @Test
    public void testUpdateTask_Success() {
        Long taskId = 1L;

        // Criação do DTO de requisição com novos valores
        TaskRequestUpdateDTO requestDTO = new TaskRequestUpdateDTO(
                "Novo título",
                "Nova descrição",
                LocalDate.now().plusDays(1));

        // Criação de uma TaskList simulada
        TaskList taskList = new TaskList();
        taskList.setId(1L);
        taskList.setTitle("Lista de Tarefas Teste");

        // Criação de uma Task simulada e associação com a TaskList
        Task task = new Task();
        task.setId(taskId);
        task.setTaskList(taskList);
        task.setTitle("Título Antigo");
        task.setDescription("Descrição Antiga");
        task.setDueDate(LocalDate.now());
        task.setStatus(Status.PENDING);

        // Configura o comportamento do mock para findById
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Configura o comportamento do mock para save
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // Chamada ao método updateTask
        TaskResponseDTO responseDTO = taskService.updateTask(taskId, requestDTO);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Novo título", responseDTO.title());
        assertEquals("Nova descrição", responseDTO.description());
        assertEquals(requestDTO.dueDate(), responseDTO.dueDate());

        // Verifica se o status foi atualizado para PENDING
        assertEquals(Status.PENDING, task.getStatus());
    }


    // Teste de falha para atualização de tarefa quando a tarefa não é encontrada
    @Test
    public void testUpdateTask_TaskNotFound() {
        Long taskId = 1L;
        TaskRequestUpdateDTO requestDTO = new TaskRequestUpdateDTO(
                "Novo título",
                "Nova descrição",
                LocalDate.now().plusDays(1));

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskId, requestDTO));
    }

    @Test
    public void testDeleteTask_ThrowsResourceNotFoundException() {
        // Tente excluir uma tarefa que não existe
        Long nonExistentTaskId = 999L;

        // Verifique que a exceção é lançada
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTask(nonExistentTaskId);
        });
    }

    // Teste de sucesso para completar tarefa
    @Test
    void testCompleteTask_Success() {
        // Criação de uma TaskList simulada
        TaskList taskList = new TaskList();
        taskList.setId(1L);
        taskList.setTitle("Lista de Tarefas Teste");

        // Criação de uma Task simulada
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Tarefa Teste");
        task.setStatus(Status.PENDING);
        task.setTaskList(taskList); // Associação da Task com a TaskList

        // Configura o comportamento do mock para findById
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Chamada ao método completeTask
        TaskResponseDTO responseDTO = taskService.completeTask(1L);

        // Verifica se o status foi atualizado corretamente
        assertEquals(Status.COMPLETED, task.getStatus());

        // Verifica se a conversão para DTO foi chamada
        verify(taskRepository).save(task);
    }


    // Teste de falha para completar tarefa quando a tarefa não é encontrada
    @Test
    public void testCompleteTask_TaskNotFound() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.completeTask(taskId));
    }

    // Teste de sucesso para obter tarefas por lista
    @Test
    public void testGetTasksByList_Success() {
        Long taskListId = 1L;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 5);

        TaskList taskList = new TaskList();
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

        Task task = new Task();
        Page<Task> tasksPage = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.findByTaskListId(taskListId, PageRequest.of(page, 5))).thenReturn(tasksPage);

        Page<Task> responseEntityTask = this.taskRepository.findByTaskListId(taskListId, pageable);

        assertNotNull(responseEntityTask);
        assertEquals(1, responseEntityTask.getTotalElements());
    }

    // Teste de falha para obter tarefas por lista quando a lista não é encontrada
    @Test
    public void testGetTasksByList_TaskListNotFound() {
        Long taskListId = 1L;
        int page = 0;

        when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTasksByList(taskListId, page));
    }

}
