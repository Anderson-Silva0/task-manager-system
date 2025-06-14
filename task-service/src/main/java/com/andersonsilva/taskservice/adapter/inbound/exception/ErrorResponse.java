package com.andersonsilva.taskservice.adapter.inbound.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> messages
) {}
