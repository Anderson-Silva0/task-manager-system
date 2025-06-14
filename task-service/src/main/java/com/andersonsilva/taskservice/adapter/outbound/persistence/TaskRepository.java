package com.andersonsilva.taskservice.adapter.outbound.persistence;

import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByStatus(TaskStatus status);

    List<TaskEntity> findByUserId(Long userId);

    List<TaskEntity> findByStatusAndUserId(TaskStatus status, Long userId);

    long countByUserId(Long userId);

}
