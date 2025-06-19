package com.andersonsilva.taskservice.application.service;

import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ITaskService {

    List<TaskEntity> findTasksByStatusAndUserId(TaskStatus status, Long userId);

    TaskEntity findById(Long id);

    TaskEntity createTask(Long userId, String title, String description, LocalDateTime deadline);

    TaskEntity updateTask(Long id, String title, String description, TaskStatus status, LocalDateTime deadline);

    void deleteTask(Long id);

    long countTasksByUserId(Long userId);

}
