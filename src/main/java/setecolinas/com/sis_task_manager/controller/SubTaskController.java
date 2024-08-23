package setecolinas.com.sis_task_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.dto.SubTaskRequestDTO;
import setecolinas.com.sis_task_manager.dto.SubTaskResponseDTO;
import setecolinas.com.sis_task_manager.service.SubTaskService;

@RestController
@RequestMapping("/subtasks")
public class SubTaskController {

    private final SubTaskService subTaskService;

    public SubTaskController(SubTaskService subTaskService) {
        this.subTaskService = subTaskService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @PostMapping
    public ResponseEntity<SubTaskResponseDTO> createSubTask(@RequestBody SubTaskRequestDTO requestDTO) {
        SubTaskResponseDTO responseDTO = subTaskService.createSubTask(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubTaskResponseDTO> getSubTaskById(@PathVariable Long id) {
        SubTaskResponseDTO responseDTO = subTaskService.getSubTaskById(id);
        return ResponseEntity.ok(responseDTO);
    }
}

