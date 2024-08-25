package setecolinas.com.sis_task_manager.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "login_attempt")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private boolean success;
    private LocalDateTime createdAt;

    public LoginAttempt() {}

    public LoginAttempt(String email, boolean success, LocalDateTime createdAt) {
        this.email = email;
        this.success = success;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginAttempt that = (LoginAttempt) o;
        return success == that.success && Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, success, createdAt);
    }

    @Override
    public String toString() {
        return "LoginAttempt{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", success=" + success +
                ", createdAt=" + createdAt +
                '}';
    }
}
