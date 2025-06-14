package com.andersonsilva.taskservice.application.dto;

import com.andersonsilva.taskservice.domain.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        Long userId,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime deadline
) {}
