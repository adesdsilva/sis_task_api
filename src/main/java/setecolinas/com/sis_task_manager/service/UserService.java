package setecolinas.com.sis_task_manager.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setecolinas.com.sis_task_manager.dto.UserRequestDTO;
import setecolinas.com.sis_task_manager.dto.UserResponseDTO;
import setecolinas.com.sis_task_manager.model.LoginAttempt;
import setecolinas.com.sis_task_manager.model.User;
import setecolinas.com.sis_task_manager.repository.UserRepository;
import setecolinas.com.sis_task_manager.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;

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
        validateUserRequest(userRequestDTO);
        checkIfEmailExists(userRequestDTO.email());

        User user = new User(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        User createdUser = userRepository.save(user);
        String token = tokenService.generateToken(createdUser.getPassword());

        return new UserResponseDTO(
                createdUser.getName(),
                createdUser.getEmail(),
                token
        );
    }

    private void checkIfEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }
    }

    private void validateUserRequest(UserRequestDTO userRequestDTO) {
        if (userRequestDTO.name() == null || userRequestDTO.name().length() <= 10) {
            throw new IllegalArgumentException("O nome do usuário deve ter mais de 10 caracteres.");
        }

        if (userRequestDTO.email() == null || !isValidEmail(userRequestDTO.email())) {
            throw new IllegalArgumentException("O e-mail fornecido é inválido.");
        }

        if (userRequestDTO.password() == null || !isValidPassword(userRequestDTO.password())) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres e conter letras e números.");
        }
    }

    private boolean isValidEmail(String email) {
        // Utiliza uma expressão regular simples para validação de e-mail
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        // Verifica se a senha tem pelo menos 6 caracteres e contém letras e números
        return password.length() >= 6 && password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }

}
