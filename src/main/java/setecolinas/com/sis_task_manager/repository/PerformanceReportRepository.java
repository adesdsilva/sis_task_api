package setecolinas.com.sis_task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import setecolinas.com.sis_task_manager.model.PerformanceReport;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceReportRepository extends JpaRepository<PerformanceReport, Long> {

    List<PerformanceReport> findByUserIdAndReportDateBetween(Long userId, LocalDateTime startDate,
                                                             LocalDateTime endDate);
}

