package setecolinas.com.sis_task_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.config.ResourceNotFoundException;
import setecolinas.com.sis_task_manager.dto.TaskListRequestDTO;
import setecolinas.com.sis_task_manager.dto.TaskListResponseDTO;
import setecolinas.com.sis_task_manager.dto.TaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.TaskResponseDTO;
import setecolinas.com.sis_task_manager.model.enums.Status;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.model.TaskList;
import setecolinas.com.sis_task_manager.repository.TaskListRepository;
import setecolinas.com.sis_task_manager.repository.TaskRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TaskListService {

    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;

    public TaskListService(TaskListRepository taskListRepository, TaskRepository taskRepository) {
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskListResponseDTO createTaskList(TaskListRequestDTO requestDTO) {
        log.info("Creating a new TaskList with title: {}", requestDTO.title());
        TaskList taskList = new TaskList();
        taskList.setTitle(requestDTO.title());
        taskList.setCreatedDate(new Date());
        taskList.setFavorite(false);

        taskList = taskListRepository.save(taskList);

        if (taskList == null || taskList.getId() == null) {
            log.error("Failed to save TaskList");
            throw new ResourceNotFoundException("A TaskList não pôde ser salva corretamente.");
        }

        log.info("TaskList saved with ID: {}", taskList.getId());

        if (requestDTO.tasks() != null) {
            for (TaskRequestDTO taskDTO : requestDTO.tasks()) {
                Task task = new Task();
                task.setTitle(taskDTO.title());
                task.setDescription(taskDTO.description());
                task.setDueDate(taskDTO.dueDate());
                task.setStatus(Status.PENDING);
                task.setTaskList(taskList);

                taskRepository.save(task);
                log.info("Task with title '{}' added to TaskList ID: {}", taskDTO.title(), taskList.getId());
            }
        }

        TaskListResponseDTO responseDTO = new TaskListResponseDTO(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getTasks().stream()
                        .map(this::convertToDTO)
                        .toList(),
                taskList.isFavorite()
        );

        log.info("TaskListResponseDTO created for TaskList ID: {}", taskList.getId());
        return responseDTO;
    }

    private TaskResponseDTO convertToDTO(Task task) {
        log.debug("Converting Task to DTO for Task ID: {}", task.getId());
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus().name(),
                task.getTaskList().getId()
        );
    }

    private TaskListResponseDTO convertToTaskListResponseDTO(TaskList taskList) {
        log.debug("Converting TaskList to TaskListResponseDTO for TaskList ID: {}", taskList.getId());
        List<TaskResponseDTO> tasks = taskList.getTasks().stream()
                .map(task -> new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getDueDate(),
                        task.getStatus().name(),
                        task.getTaskList().getId()
                ))
                .collect(Collectors.toList());

        return new TaskListResponseDTO(
                taskList.getId(),
                taskList.getTitle(),
                tasks,
                taskList.isFavorite()
        );
    }

    @Transactional
    public Page<TaskListResponseDTO> getTaskListsOrderedByFavoritesAndCreation(Pageable pageable) {
        log.info("Retrieving TaskLists ordered by favorites and creation date.");
        return taskListRepository.findAllByOrderByIsFavoriteDescCreatedDateAsc(pageable)
                .map(this::convertToTaskListResponseDTO);
    }

    @Transactional
    public Page<TaskListResponseDTO> getAllTaskLists(Pageable pageable) {
        log.info("Retrieving all TaskLists with pagination.");
        Page<TaskList> taskLists = taskListRepository.findAll(pageable);
        taskLists.forEach(taskList -> Hibernate.initialize(taskList.getTasks()));
        return taskLists.map(this::convertToTaskListResponseDTO);
    }
}

