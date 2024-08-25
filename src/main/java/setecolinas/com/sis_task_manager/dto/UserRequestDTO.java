package setecolinas.com.sis_task_manager.dto;

import setecolinas.com.sis_task_manager.model.enums.Role;

import java.util.Set;

public record UserRequestDTO(
        String name,
        String email,
        String password,
        Set<Role> roles
) {
}
