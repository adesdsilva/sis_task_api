package setecolinas.com.sis_task_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.config.ResourceNotFoundException;
import setecolinas.com.sis_task_manager.dto.TaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.TaskRequestUpdateDTO;
import setecolinas.com.sis_task_manager.dto.TaskResponseDTO;
import setecolinas.com.sis_task_manager.model.enums.Status;
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
@Transactional(readOnly = true)
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private static final Logger logger = Logger.getLogger(TaskService.class.getName());

    public TaskService(TaskRepository taskRepository, TaskListRepository taskListRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
    }

    @Transactional
    public TaskResponseDTO createTask(Long taskListId, TaskRequestDTO requestDTO) {
        log.info("Inicializando 'createTask'... {} | Task List Id: {}", requestDTO.toString(), taskListId);
        Optional<TaskList> optionalTaskList = taskListRepository.findById(taskListId);
        if (optionalTaskList.isEmpty() || Objects.isNull(optionalTaskList.get())) {
            log.info("Task List não encontrada com o Id: {}", taskListId);
            throw new ResourceNotFoundException("Lista de tarefas não encontrada");
        }
        TaskList taskList = optionalTaskList.get();
        verifyTitle(requestDTO);
        Task task = new Task();
        task.setTitle(requestDTO.title());
        task.setDescription(requestDTO.description());
        task.setDueDate(requestDTO.dueDate());
        task.setStatus(Status.PENDING);
        task.setTaskList(taskList);
        task = taskRepository.save(task);
        log.info("Task criada com sucesso no DB. Task: " + task.toString());
        return convertToDTO(task);
    }

    @Transactional
    public TaskResponseDTO updateTask(Long taskId, TaskRequestUpdateDTO requestDTO) {
        log.info("Inicializando 'updateTask'... {} | Task Id: {}", requestDTO.toString(), taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty() || Objects.isNull(optionalTask.get())) {
            log.info("Tarefa não encontrada com o Id: {}", taskId);
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        Task task = optionalTask.get();
        verifyTitleUpdateDTO(requestDTO);
        task.setTitle(requestDTO.title());
        task.setDescription(requestDTO.description());
        task.setDueDate(requestDTO.dueDate());
        task = taskRepository.save(task);
        log.info("Task atualizada com sucesso no DB. Task: {}", task.toString());
        return convertToDTO(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        log.info("Inicializando 'deleteTask'... Task Id: {}", taskId);
        if (!taskRepository.existsById(taskId)) {
            log.info("Tarefa não encontrada com o Id: {}", taskId);
            throw new ResourceNotFoundException("A Id informada: " + taskId + ", não corresponde a nenhuma Task!");
        }
        taskRepository.deleteById(taskId);
        log.info("Task deletada com sucesso do DB. Task Id: {}", taskId);
    }

    @Transactional
    public TaskResponseDTO completeTask(Long taskId) {
        log.info("Inicializando 'completeTask'... Task Id: {}", taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty() || Objects.isNull(optionalTask)) {
            log.info("Tarefa não encontrada com o Id: {}", taskId);
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        Task task = optionalTask.get();
        task.setStatus(Status.COMPLETED);
        taskRepository.save(task);
        log.info("Task completada com sucesso no DB. Task: " + task.toString());
        return convertToDTO(task);
    }

    @Transactional
    public Page<TaskResponseDTO> getTasksByList(Long taskListId, int page) {
        log.info("Inicializando 'getTasksByList'... Task List Id: {} | Page: {}", taskListId, page);
        Optional<TaskList> optionalTaskList = taskListRepository.findById(taskListId);
        if (optionalTaskList.isEmpty() || Objects.isNull(optionalTaskList)) {
            log.info("Task List não encontrada com o Id: {}", taskListId);
            throw new ResourceNotFoundException("Lista de tarefas não encontrada");
        }
        TaskList taskList = optionalTaskList.get();
        Pageable pageable = PageRequest.of(page, 5);
        Page<TaskResponseDTO> taskResponseDTOPage = taskRepository.findByTaskListId(taskList.getId(), pageable)
                .map(this::convertToDTO);
        log.info("Tasks obtidas com sucesso. Task List Id: {} | Page: {}", taskListId, page);
        return taskResponseDTOPage;
    }

    @Transactional
    public Page<TaskResponseDTO> getTasks(boolean completed, boolean favorite, Pageable pageable) {
        log.info("Inicializando 'getTasks'... Completed: {} | Favorite: {}", completed, favorite);
        Status status = completed ? Status.COMPLETED : Status.PENDING;
        Page<Task> tasksPage = taskRepository.findByStatusAndTaskListIsFavorite(status, favorite, pageable);
        log.info("Tasks filtradas obtidas com sucesso. Completed: {} | Favorite: {}", completed, favorite);
        return tasksPage.map(this::convertToDTO);
    }

    @Transactional
    public Page<TaskResponseDTO> getTasksByPending(boolean pending, Pageable pageable) {
        log.info("Inicializando 'getTasksByPending'... Pending: {}", pending);
        Status status = pending ? Status.COMPLETED : Status.PENDING;
        Page<Task> tasksPage = taskRepository.findByStatus(status, pageable);
        log.info("Tasks por pendência obtidas com sucesso. Pending: {}", pending);
        return tasksPage.map(this::convertToDTO);
    }

    @Transactional
    public Page<TaskResponseDTO> getTasksByCompletion(boolean completed, Pageable pageable) {
        log.info("Inicializando 'getTasksByCompletion'... Completed: {}", completed);
        Status status = completed ? Status.PENDING : Status.COMPLETED;
        Page<Task> tasksPage = taskRepository.findByStatus(status, pageable);
        log.info("Tasks por conclusão obtidas com sucesso. Completed: {}", completed);
        return tasksPage.map(this::convertToDTO);
    }

    @Transactional
    public Page<TaskResponseDTO> getTasksByFavorite(boolean favorite, Pageable pageable) {
        log.info("Inicializando 'getTasksByFavorite'... Favorite: {}", favorite);
        Page<Task> tasksPage = taskRepository.findByIsFavorite(favorite, pageable);
        log.info("Tasks por favorito obtidas com sucesso. Favorite: {}", favorite);
        return tasksPage.map(this::convertToDTO);
    }

    @Transactional
    public void deleteCompletedTasksFromList(Long taskListId) {
        log.info("Inicializando 'deleteCompletedTasksFromList'... Task List Id: {}", taskListId);
        List<Task> completedTasks = taskRepository.findByTaskListAndStatus(
                taskListRepository.findById(taskListId).orElseThrow(() -> new ResourceNotFoundException("TaskList não encontrado")),
                Status.COMPLETED);
        if (completedTasks.isEmpty()) {
            log.info("Nenhuma tarefa COMPLETED encontrada na lista com o Id: {}", taskListId);
            throw new ResourceNotFoundException("Não existe nenhuma Tarefa com Status COMPLETED.");
        }
        taskRepository.deleteAll(completedTasks);
        log.info("Tarefas COMPLETED deletadas com sucesso da lista. Task List Id: {}", taskListId);
    }

    @Transactional
    public TaskResponseDTO updateTaskStatus(Long taskId, Status newStatus) {
        log.info("Inicializando 'updateTaskStatus'... Task Id: {} | New Status: {}", taskId, newStatus);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada com id: " + taskId));
        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        log.info("Status da Task atualizado com sucesso. Task Id: {} | New Status: {}", taskId, newStatus);
        return convertToDTO(updatedTask);
    }

    @Transactional
    public TaskResponseDTO updateTaskFavoriteStatus(Long taskId, boolean isFavorite) {
        log.info("Inicializando 'updateTaskFavoriteStatus'... Task Id: {} | Is Favorite: {}", taskId, isFavorite);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada com id: " + taskId));
        task.setFavorite(isFavorite);
        Task updatedTask = taskRepository.save(task);
        log.info("Status de favorito da Task atualizado com sucesso. Task Id: {} | Is Favorite: {}", taskId, isFavorite);
        return convertToDTO(updatedTask);
    }

    private TaskResponseDTO convertToDTO(Task task) {
        return new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getStatus().name(), task.getTaskList().getId());
    }

    private void verifyTitle(TaskRequestDTO requestDTO) {
        if (requestDTO.title() == null || requestDTO.title().length() < 5) {
            throw new ResourceNotFoundException("O título deve ter pelo menos 5 caracteres.");
        }

        if (requestDTO.description() == null || requestDTO.description().isEmpty()) {
            throw new ResourceNotFoundException("A descrição não pode ser vazia.");
        }
    }

    private void verifyTitleUpdateDTO(TaskRequestUpdateDTO requestDTO) {
        if (requestDTO.title() == null || requestDTO.title().length() < 5) {
            throw new ResourceNotFoundException("O título deve ter pelo menos 5 caracteres.");
        }

        if (requestDTO.description() == null || requestDTO.description().isEmpty()) {
            throw new ResourceNotFoundException("A descrição não pode ser vazia.");
        }
    }
}




