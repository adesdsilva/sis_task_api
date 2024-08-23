package setecolinas.com.sis_task_manager.service;

import org.springframework.stereotype.Service;
import setecolinas.com.sis_task_manager.dto.SubTaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.SubTaskResponseDTO;
import setecolinas.com.sis_task_manager.model.SubTask;
import setecolinas.com.sis_task_manager.model.Task;
import setecolinas.com.sis_task_manager.repository.SubTaskRepository;
import setecolinas.com.sis_task_manager.repository.TaskRepository;

@Service
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;

    public SubTaskService(SubTaskRepository subTaskRepository, TaskRepository taskRepository) {
        this.subTaskRepository = subTaskRepository;
        this.taskRepository = taskRepository;
    }

    public SubTaskResponseDTO createSubTask(SubTaskRequestDTO requestDTO) {
        Task task = taskRepository.findById(requestDTO.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));

        SubTask subTask = new SubTask();
        subTask.setName(requestDTO.name());
        subTask.setTask(task);

        SubTask savedSubTask = subTaskRepository.save(subTask);

        return new SubTaskResponseDTO(savedSubTask.getId(), savedSubTask.getName(), savedSubTask.getTask().getId());
    }

    public SubTaskResponseDTO getSubTaskById(Long id) {
        SubTask subTask = subTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subtarefa não encontrada"));

        return new SubTaskResponseDTO(subTask.getId(), subTask.getName(), subTask.getTask().getId());
    }
}

