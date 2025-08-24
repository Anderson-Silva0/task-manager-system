package com.andersonsilva.taskservice.adapter.outbound.persistence;

import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("""
                SELECT t FROM TaskEntity t
                WHERE (:status IS NULL OR t.status = :status)
                    AND (:userId IS NULL OR t.userId = :userId)
                ORDER BY t.createdAt DESC
            """)
    List<TaskEntity> findTasks(@Param("status") TaskStatus status, @Param("userId") Long userId);

    long countByUserId(Long userId);

}
