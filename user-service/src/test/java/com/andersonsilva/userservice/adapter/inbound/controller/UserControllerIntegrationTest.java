package com.andersonsilva.userservice.adapter.inbound.controller;

import com.andersonsilva.userservice.adapter.outbound.persistence.UserRepository;
import com.andersonsilva.userservice.domain.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Testes de criação de usuário")
    class CreateUserTests {
        @Test
        @DisplayName("Deve criar um novo usuário com sucesso")
        void shouldCreateNewUser() throws Exception {
            var request = """
                    {
                        "name": "Anderson Silva",
                        "email": "anderson@example.com"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Anderson Silva"))
                    .andExpect(jsonPath("$.email").value("anderson@example.com"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("Deve retornar erro ao tentar criar usuário com email duplicado")
        void shouldReturnErrorWhenCreatingUserWithDuplicateEmail() throws Exception {
            var user = UserEntity.builder()
                    .name("Anderson Silva")
                    .email("anderson@example.com")
                    .build();
            userRepository.save(user);

            var request = """
                    {
                        "name": "Outro Usuário",
                        "email": "anderson@example.com"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve validar campos obrigatórios ao criar usuário")
        void shouldValidateRequiredFields() throws Exception {
            var request = """
                    {
                        "name": "",
                        "email": "email-invalido"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Testes de listagem de usuários")
    class ListUsersTests {
        @Test
        @DisplayName("Deve listar todos os usuários")
        void shouldListAllUsers() throws Exception {
            var users = List.of(
                    UserEntity.builder()
                            .name("Anderson")
                            .email("anderson@example.com")
                            .build(),
                    UserEntity.builder()
                            .name("Maria")
                            .email("maria@example.com")
                            .build()
            );
            userRepository.saveAll(users);

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].name").value("Anderson"))
                    .andExpect(jsonPath("$[0].email").value("anderson@example.com"))
                    .andExpect(jsonPath("$[1].name").value("Maria"))
                    .andExpect(jsonPath("$[1].email").value("maria@example.com"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há usuários")
        void shouldReturnEmptyListWhenNoUsers() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Testes de busca de usuário por ID")
    class FindUserByIdTests {
        @Test
        @DisplayName("Deve buscar usuário por ID")
        void shouldFindUserById() throws Exception {
            var user = UserEntity.builder()
                    .name("Anderson Silva")
                    .email("anderson@example.com")
                    .build();
            var savedUser = userRepository.save(user);

            mockMvc.perform(get("/api/users/{id}", savedUser.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Anderson Silva"))
                    .andExpect(jsonPath("$.email").value("anderson@example.com"));
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar usuário inexistente")
        void shouldReturn404WhenUserNotFound() throws Exception {
            mockMvc.perform(get("/api/users/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes de atualização de usuário")
    class UpdateUserTests {
        @Test
        @DisplayName("Deve atualizar usuário existente")
        void shouldUpdateExistingUser() throws Exception {
            var user = UserEntity.builder()
                    .name("Anderson Silva")
                    .email("anderson@example.com")
                    .build();
            var savedUser = userRepository.save(user);

            var request = """
                    {
                        "name": "Anderson Silva Atualizado",
                        "email": "anderson.novo@example.com"
                    }
                    """;

            mockMvc.perform(put("/api/users/{id}", savedUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Anderson Silva Atualizado"))
                    .andExpect(jsonPath("$.email").value("anderson.novo@example.com"));
        }

        @Test
        @DisplayName("Deve retornar 404 ao tentar atualizar usuário inexistente")
        void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
            var request = """
                    {
                        "name": "Anderson Silva",
                        "email": "anderson@example.com"
                    }
                    """;

            mockMvc.perform(put("/api/users/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve validar campos obrigatórios ao atualizar usuário")
        void shouldValidateRequiredFieldsOnUpdate() throws Exception {
            var user = UserEntity.builder()
                    .name("Anderson Silva")
                    .email("anderson@example.com")
                    .build();
            var savedUser = userRepository.save(user);

            var request = """
                    {
                        "name": "",
                        "email": "email-invalido"
                    }
                    """;

            mockMvc.perform(put("/api/users/{id}", savedUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }
    }
} 