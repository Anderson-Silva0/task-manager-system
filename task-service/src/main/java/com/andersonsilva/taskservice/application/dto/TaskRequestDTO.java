package com.andersonsilva.taskservice.application.dto;

import com.andersonsilva.taskservice.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRequestDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        Long userId,

        @NotBlank(message = "Título é obrigatório")
        String title,

        String description,

        TaskStatus status,

        LocalDateTime deadline
) {}
