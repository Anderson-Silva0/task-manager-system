package com.andersonsilva.taskservice.application.dto;

import com.andersonsilva.taskservice.domain.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        Long userId,
        String title,
        String description,
        TaskStatus status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime deadline
) {}
