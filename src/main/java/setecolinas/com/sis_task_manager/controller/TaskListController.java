package setecolinas.com.sis_task_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.dto.CustomPageResponse;
import setecolinas.com.sis_task_manager.dto.TaskListRequestDTO;
import setecolinas.com.sis_task_manager.dto.TaskListResponseDTO;
import setecolinas.com.sis_task_manager.service.TaskListService;

@RestController
@RequestMapping("/task-lists")
public class TaskListController {

    private final TaskListService taskListService;

    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @PostMapping
    public ResponseEntity<TaskListResponseDTO> createTaskList(
            @RequestBody TaskListRequestDTO requestDTO
    ) {
        TaskListResponseDTO responseDTO = taskListService.createTaskList(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/ordered")
    public ResponseEntity<CustomPageResponse<TaskListResponseDTO>> getOrderedTaskLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TaskListResponseDTO> taskLists = taskListService.getTaskListsOrderedByFavoritesAndCreation(pageable);

        CustomPageResponse<TaskListResponseDTO> response = new CustomPageResponse<>(
                taskLists.getContent(),
                taskLists.getNumber(),
                taskLists.getSize(),
                taskLists.getTotalElements(),
                taskLists.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<CustomPageResponse<TaskListResponseDTO>> getTaskLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TaskListResponseDTO> taskLists = taskListService.getAllTaskLists(pageable);

        CustomPageResponse<TaskListResponseDTO> response = new CustomPageResponse<>(
                taskLists.getContent(),
                taskLists.getNumber(),
                taskLists.getSize(),
                taskLists.getTotalElements(),
                taskLists.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}

