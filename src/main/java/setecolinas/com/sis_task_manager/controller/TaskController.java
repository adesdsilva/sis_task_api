package setecolinas.com.sis_task_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.dto.*;
import setecolinas.com.sis_task_manager.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    // Endpoint para criar uma task
    @PostMapping("/{listId}")
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable Long listId,
            @RequestBody TaskRequestDTO requestDTO
    ) {
        TaskResponseDTO responseDTO = taskService.createTask(listId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // Endpoint para listar as tasks, de acordo com a lista de tarefas
    @GetMapping("/list/{listId}")
    public ResponseEntity<CustomPageResponse<TaskResponseDTO>> getTasksByList(
            @PathVariable Long listId,
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<TaskResponseDTO> responseDTOs = taskService.getTasksByList(listId, page);

        CustomPageResponse<TaskResponseDTO> response = new CustomPageResponse<>(
                responseDTOs.getContent(),
                responseDTOs.getNumber(),
                responseDTOs.getSize(),
                responseDTOs.getTotalElements(),
                responseDTOs.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    // Endpoint para editar task
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskRequestUpdateDTO requestDTO
    ) {
        TaskResponseDTO responseDTO = taskService.updateTask(taskId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // Endpoint para deletar task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para alterar o status da task para COMPLETED
    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponseDTO> completeTask(@PathVariable Long taskId) {
        TaskResponseDTO responseDTO = taskService.completeTask(taskId);
        return ResponseEntity.ok(responseDTO);
    }

    // Endpoint para filtrar tarefas com base em todos os filtros
    @GetMapping("/filter")
    public ResponseEntity<CustomPageResponse<TaskResponseDTO>> getFilteredTasks(
            @RequestParam(value = "completed", required = false) Boolean completed,
            @RequestParam(value = "favorite", required = false) Boolean favorite,
            Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskService.getTasks(
                completed != null ? completed : false,
                favorite != null ? favorite : false,
                pageable);
        CustomPageResponse<TaskResponseDTO> response = new CustomPageResponse<>(
                tasks.getContent(),
                tasks.getNumber(),
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    // Endpoint para filtrar apenas por tarefas concluídas
    @GetMapping("/completed")
    public ResponseEntity<CustomPageResponse<TaskResponseDTO>> getTasksByCompletion(
            @RequestParam(value = "completed", required = false) Boolean completed,
            Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskService.getTasksByCompletion(
                completed != null ? completed : false,
                pageable);

        CustomPageResponse<TaskResponseDTO> response = new CustomPageResponse<>(
                tasks.getContent(),
                tasks.getNumber(),
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    // Endpoint para filtrar apenas por tarefas não concluídas
    @GetMapping("/pending")
    public ResponseEntity<CustomPageResponse<TaskResponseDTO>> getTasksByPending(
            @RequestParam(value = "pending", required = false) Boolean pending,
            Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskService.getTasksByPending(
                pending != null ? pending : false,
                pageable);

        CustomPageResponse<TaskResponseDTO> response = new CustomPageResponse<>(
                tasks.getContent(),
                tasks.getNumber(),
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    // Endpoint para filtrar tarefas favoritas
    @GetMapping("/favorite")
    public ResponseEntity<CustomPageResponse<TaskResponseDTO>> getTasksByFavorite(
            @RequestParam(value = "favorite", required = false) Boolean favorite,
            Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskService.getTasksByFavorite(
                favorite != null ? favorite : true,
                pageable);

        CustomPageResponse<TaskResponseDTO> response = new CustomPageResponse<>(
                tasks.getContent(),
                tasks.getNumber(),
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    // Endpoint para excluir tarefas concluídas de uma lista
    @DeleteMapping("/lists/{taskListId}/completed")
    public ResponseEntity<Void> deleteCompletedTasksFromList(@PathVariable Long taskListId) {
        taskService.deleteCompletedTasksFromList(taskListId);
        return ResponseEntity.noContent().build();
    }

    //Endpoint para atualizar status p/ COMPLETO ou PENDENTE
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody StatusUpdateRequest statusUpdateRequest) {
        TaskResponseDTO updatedTask = taskService.updateTaskStatus(taskId, statusUpdateRequest.status());
        return ResponseEntity.ok(updatedTask);
    }

    //Endpoint para favoritar/desfavoritar uma tarefa/subtarefa
    @PatchMapping("/{taskId}/favorite")
    public ResponseEntity<TaskResponseDTO> updateTaskFavorite(
            @PathVariable Long taskId,
            @RequestBody FavoriteUpdateRequest favoriteUpdateRequest) {
        TaskResponseDTO updatedTask =
                taskService.updateTaskFavoriteStatus(taskId, favoriteUpdateRequest.isFavorite());
        return ResponseEntity.ok(updatedTask);
    }
}
