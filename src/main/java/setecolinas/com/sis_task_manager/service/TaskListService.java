package setecolinas.com.sis_task_manager.service;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

@Service
public class TaskListService {

    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;

    public TaskListService(TaskListRepository taskListRepository, TaskRepository taskRepository) {
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
    }

    public TaskListResponseDTO createTaskList(TaskListRequestDTO requestDTO) {
        TaskList taskList = new TaskList();
        taskList.setTitle(requestDTO.title());
        taskList.setCreatedDate(new Date()); // Defina a data de criação se necessário
        taskList.setFavorite(false); // Defina um valor padrão para isFavorite

        // Salva a lista de tarefas
        taskList = taskListRepository.save(taskList);

        // Verifique se a lista foi salva corretamente
        if (taskList == null || taskList.getId() == null) {
            throw new ResourceNotFoundException("A TaskList não pôde ser salva corretamente.");
        }

        // Crie e associe as tarefas à nova lista de tarefas
        if (requestDTO.tasks() != null) {
            for (TaskRequestDTO taskDTO : requestDTO.tasks()) {
                Task task = new Task();
                task.setTitle(taskDTO.title());
                task.setDescription(taskDTO.description());
                task.setDueDate(taskDTO.dueDate());
                task.setStatus(Status.PENDING);
                task.setTaskList(taskList);

                taskRepository.save(task);
            }
        }

        // Crie o DTO de resposta
        TaskListResponseDTO responseDTO = new TaskListResponseDTO(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getTasks().stream()
                        .map(this::convertToDTO)
                        .toList(),
                taskList.isFavorite()
        );

        return responseDTO;
    }



    private TaskResponseDTO convertToDTO(Task task) {
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

    public Page<TaskListResponseDTO> getTaskListsOrderedByFavoritesAndCreation(Pageable pageable) {
        return taskListRepository.findAllByOrderByIsFavoriteDescCreatedDateAsc(pageable)
                .map(this::convertToTaskListResponseDTO);
    }

    @Transactional
    public Page<TaskListResponseDTO> getAllTaskLists(Pageable pageable) {
        Page<TaskList> taskLists = taskListRepository.findAll(pageable);
        taskLists.forEach(taskList -> Hibernate.initialize(taskList.getTasks()));
        return taskLists.map(this::convertToTaskListResponseDTO);
    }

}

