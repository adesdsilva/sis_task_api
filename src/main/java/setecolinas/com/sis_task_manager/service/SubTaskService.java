package setecolinas.com.sis_task_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.dto.SubTaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.SubTaskResponseDTO;
import setecolinas.com.sis_task_manager.model.SubTask;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.repository.SubTaskRepository;
import setecolinas.com.sis_task_manager.repository.TaskRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;

    public SubTaskService(SubTaskRepository subTaskRepository, TaskRepository taskRepository) {
        this.subTaskRepository = subTaskRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public SubTaskResponseDTO createSubTask(SubTaskRequestDTO requestDTO) {
        log.info("Creating a new SubTask with name: {}", requestDTO.name());
        Task task = taskRepository.findById(requestDTO.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));

        SubTask subTask = new SubTask();
        subTask.setName(requestDTO.name());
        subTask.setTask(task);

        SubTask savedSubTask = subTaskRepository.save(subTask);

        log.info("SubTask created with ID: {}", savedSubTask.getId());
        return new SubTaskResponseDTO(savedSubTask.getId(), savedSubTask.getName(), savedSubTask.getTask().getId());
    }

    @Transactional
    public SubTaskResponseDTO getSubTaskById(Long id) {
        log.info("Fetching SubTask by ID: {}", id);
        SubTask subTask = subTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subtarefa não encontrada"));

        log.info("SubTask retrieved with ID: {}", subTask.getId());
        return new SubTaskResponseDTO(subTask.getId(), subTask.getName(), subTask.getTask().getId());
    }
}

