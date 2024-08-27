package setecolinas.com.sis_task_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.dto.UserRequestDTO;
import setecolinas.com.sis_task_manager.dto.UserResponseDTO;
import setecolinas.com.sis_task_manager.model.User;
import setecolinas.com.sis_task_manager.repository.UserRepository;
import setecolinas.com.sis_task_manager.security.JwtUtil;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        log.info("Creating a new user with email: {}", userRequestDTO.email());
        validateUserRequest(userRequestDTO);
        checkIfEmailExists(userRequestDTO.email());

        User user = new User(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        User createdUser = userRepository.save(user);
        String token = tokenService.generateToken(createdUser.getPassword());

        log.info("User created with ID: {}", createdUser.getId());
        return new UserResponseDTO(
                createdUser.getName(),
                createdUser.getEmail(),
                token
        );
    }

    private void checkIfEmailExists(String email) {
        log.debug("Checking if email already exists: {}", email);
        if (userRepository.findByEmail(email).isPresent()) {
            log.error("Email already in use: {}", email);
            throw new IllegalArgumentException("Email already in use.");
        }
    }

    private void validateUserRequest(UserRequestDTO userRequestDTO) {
        log.debug("Validating user request for email: {}", userRequestDTO.email());
        if (userRequestDTO.name() == null || userRequestDTO.name().length() <= 10) {
            log.error("Invalid user name: {}", userRequestDTO.name());
            throw new IllegalArgumentException("O nome do usuário deve ter mais de 10 caracteres.");
        }

        if (userRequestDTO.email() == null || !isValidEmail(userRequestDTO.email())) {
            log.error("Invalid email: {}", userRequestDTO.email());
            throw new IllegalArgumentException("O e-mail fornecido é inválido.");
        }

        if (userRequestDTO.password() == null || !isValidPassword(userRequestDTO.password())) {
            log.error("Invalid password for user: {}", userRequestDTO.email());
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres e conter letras e números.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }
}
