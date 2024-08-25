package setecolinas.com.sis_task_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import setecolinas.com.sis_task_manager.dto.UserRequestDTO;
import setecolinas.com.sis_task_manager.dto.UserResponseDTO;
import setecolinas.com.sis_task_manager.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }
}

