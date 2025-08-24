package com.andersonsilva.taskservice.adapter.inbound.controller.impl;

import com.andersonsilva.taskservice.adapter.outbound.client.UserClient;
import com.andersonsilva.taskservice.adapter.outbound.persistence.TaskRepository;
import com.andersonsilva.taskservice.application.dto.UserDTO;
import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerImplIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @MockitoBean
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        when(userClient.findById(any())).thenReturn(new UserDTO(1L, "Test User", "test@example.com"));
    }

    @Test
    @DisplayName("Deve criar uma nova tarefa com sucesso")
    void shouldCreateNewTask() throws Exception {
        var request = """
                {
                    "userId": 1,
                    "title": "Implementar testes",
                    "description": "Criar testes de integração para o TaskController",
                    "status": "PENDENTE",
                    "deadline": "%s"
                }
                """.formatted(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Implementar testes"))
                .andExpect(jsonPath("$.description").value("Criar testes de integração para o TaskController"))
                .andExpect(jsonPath("$.status").value(TaskStatus.PENDENTE.name()))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void shouldListAllTasks() throws Exception {
        var tasks = List.of(
                TaskEntity.builder()
                        .title("Tarefa 1")
                        .description("Descrição 1")
                        .userId(1L)
                        .status(TaskStatus.PENDENTE)
                        .build(),
                TaskEntity.builder()
                        .title("Tarefa 2")
                        .description("Descrição 2")
                        .userId(2L)
                        .status(TaskStatus.CONCLUIDO)
                        .build()
        );
        taskRepository.saveAll(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[1].title").value("Tarefa 1"))
                .andExpect(jsonPath("$[0].title").value("Tarefa 2"));
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID")
    void shouldFindTaskById() throws Exception {
        var task = TaskEntity.builder()
                .title("Implementar testes")
                .description("Criar testes de integração")
                .userId(1L)
                .status(TaskStatus.PENDENTE)
                .build();
        var savedTask = taskRepository.save(task);

        mockMvc.perform(get("/api/tasks/{id}", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Implementar testes"))
                .andExpect(jsonPath("$.description").value("Criar testes de integração"))
                .andExpect(jsonPath("$.status").value(TaskStatus.PENDENTE.name()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar tarefa inexistente")
    void shouldReturn404WhenTaskNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar tarefa existente")
    void shouldUpdateExistingTask() throws Exception {
        var task = TaskEntity.builder()
                .title("Implementar testes")
                .description("Criar testes de integração")
                .userId(1L)
                .status(TaskStatus.PENDENTE)
                .build();
        var savedTask = taskRepository.save(task);

        var request = """
                {
                    "userId": 1,
                    "title": "Implementar testes atualizado",
                    "description": "Criar testes de integração atualizado",
                    "status": "PENDENTE",
                    "deadline": "%s"
                }
                """.formatted(LocalDateTime.now().plusDays(2));

        mockMvc.perform(put("/api/tasks/{id}", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Implementar testes atualizado"))
                .andExpect(jsonPath("$.description").value("Criar testes de integração atualizado"))
                .andExpect(jsonPath("$.status").value(TaskStatus.PENDENTE.name()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar tarefa inexistente")
    void shouldReturn404WhenUpdatingNonExistentTask() throws Exception {
        var request = """
                {
                    "title": "Implementar testes",
                    "description": "Criar testes de integração",
                    "userId": 1,
                    "status": "PENDENTE",
                    "deadline": "%s"
                }
                """.formatted(LocalDateTime.now().plusDays(1));

        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve excluir tarefa existente")
    void shouldDeleteExistingTask() throws Exception {
        var task = TaskEntity.builder()
                .title("Implementar testes")
                .description("Criar testes de integração")
                .userId(1L)
                .status(TaskStatus.PENDENTE)
                .build();
        var savedTask = taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/{id}", savedTask.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", savedTask.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar excluir tarefa inexistente")
    void shouldReturn404WhenDeletingNonExistentTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }
} 