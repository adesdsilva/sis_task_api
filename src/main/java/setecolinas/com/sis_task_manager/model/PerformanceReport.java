package setecolinas.com.sis_task_manager.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "performance_reports")
public class PerformanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer completedTasks;
    private Double averageCompletionTime;
    private LocalDateTime reportDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public PerformanceReport() {}

    public PerformanceReport(User user, Integer completedTasks, Double averageCompletionTime,
                             LocalDateTime reportDate, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.completedTasks = completedTasks;
        this.averageCompletionTime = averageCompletionTime;
        this.reportDate = reportDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Double getAverageCompletionTime() {
        return averageCompletionTime;
    }

    public void setAverageCompletionTime(Double averageCompletionTime) {
        this.averageCompletionTime = averageCompletionTime;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "PerformanceReport{" +
                "id=" + id +
                ", user=" + user +
                ", completedTasks=" + completedTasks +
                ", averageCompletionTime=" + averageCompletionTime +
                ", reportDate=" + reportDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceReport that = (PerformanceReport) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(completedTasks, that.completedTasks) && Objects.equals(averageCompletionTime, that.averageCompletionTime) && Objects.equals(reportDate, that.reportDate) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, completedTasks, averageCompletionTime, reportDate, startDate, endDate);
    }
}

