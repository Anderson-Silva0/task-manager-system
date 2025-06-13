package com.andersonsilva.userservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String email

) {}
