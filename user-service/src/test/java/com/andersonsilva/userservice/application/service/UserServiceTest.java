package com.andersonsilva.userservice.application.service;

import com.andersonsilva.userservice.adapter.outbound.client.TaskClient;
import com.andersonsilva.userservice.adapter.outbound.persistence.UserRepository;
import com.andersonsilva.userservice.domain.UserEntity;
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
class UserServiceTest {

    @Mock
    private UserRepository repository;
    
    @Mock
    private TaskClient taskClient;
    
    @InjectMocks
    private UserService service;

    private UserEntity userEntity;

    @BeforeEach
    void setup() {
        userEntity = UserEntity.builder()
                .id(1L)
                .name("Anderson")
                .email("anderson@example.com")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("findAll retorna todos os usuários")
    void findAllReturnsAllUsers() {
        when(repository.findAll()).thenReturn(List.of(userEntity));
        var users = service.findAll();
        assertEquals(1, users.size());
        assertSame(userEntity, users.get(0));
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findById existente retorna usuário")
    void findByIdExistingReturnsUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
        var user = service.findById(1L);
        assertEquals(userEntity, user);
    }

    @Test
    @DisplayName("findById ausente lança EntityNotFoundException")
    void findByIdMissingThrowsException() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.findById(2L));
        assertTrue(ex.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    @DisplayName("createUser com email único salva com sucesso")
    void createUserWithUniqueEmailSucceeds() {
        when(repository.existsByEmail("bob@example.com")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> {
            var u = inv.getArgument(0, UserEntity.class);
            u.setId(2L);
            return u;
        });

        var created = service.createUser("Bob", "bob@example.com");
        assertEquals(2L, created.getId());
        assertEquals("Bob", created.getName());
        verify(repository).existsByEmail("bob@example.com");
        verify(repository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("createUser com email duplicado lança IllegalArgumentException")
    void createUserWithDuplicateEmailThrowsException() {
        when(repository.existsByEmail("anderson@example.com")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createUser("Anderson", "anderson@example.com"));
        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    @DisplayName("updateUser muda dados quando email único")
    void updateUserWithUniqueEmailSucceeds() {
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(repository.existsByEmail("anderson2@example.com")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var updated = service.updateUser(1L, "Anderson A.", "anderson2@example.com");
        assertEquals("Anderson A.", updated.getName());
        assertEquals("anderson2@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("updateUser para email duplicado lança IllegalArgumentException")
    void updateUserWithDuplicateEmailThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(repository.existsByEmail("bob@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateUser(1L, "Anderson", "bob@example.com"));
        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    @DisplayName("deleteUser sem tarefas associadas exclui com sucesso")
    void deleteUserWithoutTasksSucceeds() {
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(taskClient.countTasksByUserId(1L)).thenReturn(0L);
        assertDoesNotThrow(() -> service.deleteUser(1L));
        verify(repository).delete(userEntity);
    }

    @Test
    @DisplayName("deleteUser com tarefas associadas lança IllegalStateException")
    void deleteUserWithTasksThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(taskClient.countTasksByUserId(1L)).thenReturn(5L);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.deleteUser(1L));
        assertEquals("Usuário possui tarefas associadas e não pode ser deletado.", ex.getMessage());
    }

}
