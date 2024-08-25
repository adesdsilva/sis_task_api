package setecolinas.com.sis_task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setecolinas.com.sis_task_manager.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
