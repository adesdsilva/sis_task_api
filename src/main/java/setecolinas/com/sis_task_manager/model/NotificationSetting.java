package setecolinas.com.sis_task_manager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email_notifications", nullable = false)
    private boolean emailNotifications;

    @Column(name = "push_notifications", nullable = false)
    private boolean pushNotifications;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationSetting that = (NotificationSetting) o;
        return emailNotifications == that.emailNotifications &&
                pushNotifications == that.pushNotifications &&
                Objects.equals(id, that.id) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, emailNotifications, pushNotifications);
    }

    @Override
    public String toString() {
        return "NotificationSetting{" +
                "id=" + id +
                ", user=" + user +
                ", emailNotifications=" + emailNotifications +
                ", pushNotifications=" + pushNotifications +
                '}';
    }
}
