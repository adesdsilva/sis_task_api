package setecolinas.com.sis_task_manager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import setecolinas.com.sis_task_manager.config.ResourceNotFoundException;
import setecolinas.com.sis_task_manager.dto.TaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.TaskRequestUpdateDTO;
import setecolinas.com.sis_task_manager.dto.TaskResponseDTO;
import setecolinas.com.sis_task_manager.model.Status;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.model.TaskList;
import setecolinas.com.sis_task_manager.repository.TaskListRepository;
import setecolinas.com.sis_task_manager.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private static final Logger logger = Logger.getLogger(TaskService.class.getName());

    public TaskService(TaskRepository taskRepository, TaskListRepository taskListRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
    }

    public TaskResponseDTO createTask(Long taskListId, TaskRequestDTO requestDTO) {
        // Consulta a lista de tarefas pelo ID
        Optional<TaskList> optionalTaskList = taskListRepository.findById(taskListId);

        // Verifica se a lista de tarefas foi encontrada
        if (optionalTaskList.isEmpty() || Objects.isNull(optionalTaskList.get())) {
            throw new ResourceNotFoundException("Lista de tarefas não encontrada");
        }

        // Obtém a lista de tarefas existente
        TaskList taskList = optionalTaskList.get();

        // Verifica o título da tarefa
        verifyTitle(requestDTO);

        // Cria uma nova tarefa
        Task task = new Task();
        task.setTitle(requestDTO.title());
        task.setDescription(requestDTO.description());
        task.setDueDate(requestDTO.dueDate());
        task.setStatus(Status.PENDING);
        task.setTaskList(taskList);

        // Salva a tarefa no repositório
        task = taskRepository.save(task);

        // Converte a tarefa para DTO e retorna
        return convertToDTO(task);
    }

    public TaskResponseDTO updateTask(Long taskId, TaskRequestUpdateDTO requestDTO) {
        // Consulta a tarefa pelo ID
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        // Verifica se a tarefa foi encontrada
        if (optionalTask.isEmpty() || Objects.isNull(optionalTask.get())) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }

        // Obtém a tarefa existente
        Task task = optionalTask.get();

        // Verifica o título da tarefa
        verifyTitleUpdateDTO(requestDTO);

        // Atualiza os atributos da tarefa
        task.setTitle(requestDTO.title());
        task.setDescription(requestDTO.description());
        task.setDueDate(requestDTO.dueDate());

        // Salva a tarefa atualizada no repositório
        task = taskRepository.save(task);

        // Converte a tarefa para DTO e retorna
        return convertToDTO(task);
    }

    private void verifyTitleUpdateDTO(TaskRequestUpdateDTO requestDTO) {
        if (requestDTO.title() == null || requestDTO.title().length() < 5) {
            throw new ResourceNotFoundException("O título deve ter pelo menos 5 caracteres.");
        }

        if (requestDTO.description() == null || requestDTO.description().isEmpty()) {
            throw new ResourceNotFoundException("A descrição não pode estar vazia.");
        }

        if (requestDTO.dueDate().isBefore(LocalDate.now())) {
            throw new ResourceNotFoundException("A data prevista deve ser no futuro.");
        }
    }

    private void verifyTitle(TaskRequestDTO requestDTO) {
        if (requestDTO.title() == null || requestDTO.title().length() < 5) {
            throw new ResourceNotFoundException("O título deve ter pelo menos 5 caracteres.");
        }

        if (requestDTO.description() == null || requestDTO.description().isEmpty()) {
            throw new ResourceNotFoundException("A descrição não pode estar vazia.");
        }

        if (requestDTO.dueDate().isBefore(LocalDate.now())) {
            throw new ResourceNotFoundException("A data prevista deve ser no futuro.");
        }
    }

    public void deleteTask(Long taskId) {
        if(!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("A Id informada: " + taskId + ", não corresponde a nehuma Task!");
        }
        taskRepository.deleteById(taskId);
    }

    public TaskResponseDTO completeTask(Long taskId) {
        // Consulta a tarefa pelo ID
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        // Verifica se a tarefa foi encontrada
        if (optionalTask.isEmpty() || Objects.isNull(optionalTask)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }

        // Obtém a tarefa existente
        Task task = optionalTask.get();

        // Atualiza o status da tarefa para COMPLETED
        task.setStatus(Status.COMPLETED);

        // Salva a tarefa atualizada
        taskRepository.save(task);

        // Converte a tarefa atualizada para DTO e retorna
        return convertToDTO(task);
    }

    public Page<TaskResponseDTO> getTasksByList(Long taskListId, int page) {
        // Consulta a lista de tarefas pelo ID
        Optional<TaskList> optionalTaskList = taskListRepository.findById(taskListId);

        // Verifica se a lista de tarefas foi encontrada
        if (optionalTaskList.isEmpty() || Objects.isNull(optionalTaskList)) {
            throw new ResourceNotFoundException("Lista de tarefas não encontrada");
        }

        // Obtém a lista de tarefas existente
        TaskList taskList = optionalTaskList.get();

        // Define a paginação
        Pageable pageable = PageRequest.of(page, 5);

        // Retorna a página de tarefas convertidas para DTO
        return taskRepository.findByTaskListId(taskList.getId(), pageable)
                .map(this::convertToDTO);
    }

    // Filtros aplicados
    public Page<TaskResponseDTO> getTasks(boolean completed, boolean favorite, Pageable pageable) {
        Status status = completed ? Status.COMPLETED : Status.PENDING;
        Page<Task> tasksPage = taskRepository.findByStatusAndTaskListIsFavorite(status, favorite, pageable);
        return tasksPage.map(this::convertToDTO);
    }

    // Filtro apenas por tarefas concluídas
    public Page<TaskResponseDTO> getTasksByPending(boolean pending, Pageable pageable) {
        Status status = pending ? Status.COMPLETED : Status.PENDING;
        Page<Task> tasksPage = taskRepository.findByStatus(status, pageable);
        return tasksPage.map(this::convertToDTO);
    }

    // Filtro apenas por tarefas concluídas
    public Page<TaskResponseDTO> getTasksByCompletion(boolean completed, Pageable pageable) {
        Status status = completed ? Status.PENDING : Status.COMPLETED;
        Page<Task> tasksPage = taskRepository.findByStatus(status, pageable);
        return tasksPage.map(this::convertToDTO);
    }

    // Filtro apenas por favoritas
    public Page<TaskResponseDTO> getTasksByFavorite(boolean favorite, Pageable pageable) {
        Page<Task> tasksPage = taskRepository.findByIsFavorite(favorite, pageable);
        return tasksPage.map(this::convertToDTO);
    }

    // Método para excluir tarefas concluídas de uma lista
    public void deleteCompletedTasksFromList(Long taskListId) {
        List<Task> completedTasks = taskRepository.findByTaskListAndStatus(
                taskListRepository.findById(taskListId).orElseThrow(() -> new ResourceNotFoundException("TaskList não encontrado")),
                Status.COMPLETED);

        if (completedTasks.isEmpty()) {
            throw new ResourceNotFoundException("Não existe nenhuma Tarefa com Status COMPLETED.");
        }

        taskRepository.deleteAll(completedTasks);
    }

    public TaskResponseDTO updateTaskStatus(Long taskId, Status newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada com id: " + taskId));
        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    private TaskResponseDTO convertToDTO(Task task) {
        return new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getStatus().name(), task.getTaskList().getId());
    }

    public TaskResponseDTO updateTaskFavoriteStatus(Long taskId, boolean isFavorite) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada com id: " + taskId));
        task.setFavorite(isFavorite);
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

}



