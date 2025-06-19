package com.andersonsilva.taskservice.application.service.impl;

import com.andersonsilva.taskservice.adapter.outbound.client.UserClient;
import com.andersonsilva.taskservice.adapter.outbound.persistence.TaskRepository;
import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private TaskServiceImpl service;

    private TaskEntity taskEntity;

    @BeforeEach
    void setup() {
        taskEntity = TaskEntity.builder()
                .id(1L)
                .userId(1L)
                .title("Corrigir layout da tela de login")
                .description("Revisar alinhamento e cores no formulário de login")
                .status(TaskStatus.PENDENTE)
                .createdAt(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(3))
                .build();
    }

    @Test
    @DisplayName("Listar todas as tarefas sem filtros")
    void listAllTasksWithoutFilters() {
        when(repository.findAll()).thenReturn(List.of(taskEntity));
        var resultado = service.findTasksByStatusAndUserId(null, null);
        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Listar tarefas por status e usuário")
    void listTasksByStatusAndUserId() {
        when(repository.findByStatusAndUserId(TaskStatus.PENDENTE, 1L)).thenReturn(List.of(taskEntity));
        var resultado = service.findTasksByStatusAndUserId(TaskStatus.PENDENTE, 1L);
        assertEquals(1, resultado.size());
        verify(repository).findByStatusAndUserId(TaskStatus.PENDENTE, 1L);
    }

    @Test
    @DisplayName("Buscar tarefa existente por ID")
    void findTaskByIdWhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(taskEntity));
        var tarefa = service.findById(1L);
        assertSame(taskEntity, tarefa);
    }

    @Test
    @DisplayName("Lançar exceção ao buscar tarefa inexistente")
    void findTaskByIdWhenNotExists() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.findById(99L));
        assertTrue(ex.getMessage().contains("Tarefa não encontrada"));
    }

    @Test
    @DisplayName("Criar nova tarefa para usuário válido")
    void createTaskForValidUser() {
        when(userClient.findById(1L)).thenReturn(null);
        when(repository.save(any())).thenReturn(taskEntity);

        var criada = service.createTask(1L, "Atualizar documentação", "Documentar novos endpoints", taskEntity.getDeadline());
        assertEquals(TaskStatus.PENDENTE, criada.getStatus());
        verify(userClient).findById(1L);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Lançar exceção ao criar tarefa para usuário inexistente")
    void createTaskForInvalidUserThrowsException() {
        doThrow(new RuntimeException()).when(userClient).findById(2L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createTask(2L, "Tarefa inválida", "Usuário não existe", null));
        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Lançar exceção ao tentar editar tarefa concluída")
    void updateCompletedTaskThrowsException() {
        taskEntity.setStatus(TaskStatus.CONCLUIDO);
        when(repository.findById(1L)).thenReturn(Optional.of(taskEntity));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.updateTask(1L, "Novo título", "Nova descrição", TaskStatus.EM_ANDAMENTO, null));
        assertEquals("Não é possível editar tarefas concluídas", ex.getMessage());
    }

    @Test
    @DisplayName("Atualizar dados da tarefa com sucesso")
    void updateTaskSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userClient.findById(1L)).thenReturn(null);
        when(repository.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        var atualizada = service.updateTask(1L, "Revisar testes automatizados", "Ajustar testes com Mockito", TaskStatus.EM_ANDAMENTO, taskEntity.getDeadline());

        assertEquals("Revisar testes automatizados", atualizada.getTitle());
        assertEquals(TaskStatus.EM_ANDAMENTO, atualizada.getStatus());
    }

    @Test
    @DisplayName("Excluir tarefa existente com sucesso")
    void deleteTaskSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(taskEntity));
        assertDoesNotThrow(() -> service.deleteTask(1L));
        verify(repository).delete(taskEntity);
    }

}