package setecolinas.com.sis_task_manager.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import setecolinas.com.sis_task_manager.model.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "task_list_id")
    private TaskList taskList;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SubTask> subTasks;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public boolean isCompleted() {
        return this.status == Status.COMPLETED;
    }

    public boolean isFavorite() {
        return this.taskList != null && this.taskList.isFavorite();
    }

    public void setFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && Objects.equals(dueDate, task.dueDate) && Objects.equals(status, task.status)
                && Objects.equals(taskList, task.taskList) && Objects.equals(subTasks, task.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, dueDate, status, taskList, subTasks);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", taskList=" + taskList +
                ", subTasks=" + subTasks +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
