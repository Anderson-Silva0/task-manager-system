package com.andersonsilva.taskservice.adapter.outbound.persistence;

import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    private TaskEntity build(String title, Long userId, TaskStatus status, LocalDateTime createdAt) {
        return TaskEntity.builder()
                .title(title)
                .description(title + " desc")
                .userId(userId)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    @Test
    @DisplayName("findTasks - sem filtros retorna todos ordenados por createdAt desc")
    void whenNoFilters_thenReturnAllOrderedDesc() {
        var older = build("old", 1L, TaskStatus.PENDENTE, LocalDateTime.now().minusDays(2));
        var newer = build("new", 2L, TaskStatus.CONCLUIDO, LocalDateTime.now().minusHours(1));

        repository.save(older);
        repository.save(newer);

        List<TaskEntity> result = repository.findTasks(null, null);

        assertEquals(2, result.size());
        assertEquals("new", result.get(0).getTitle());
        assertEquals("old", result.get(1).getTitle());
    }

    @Test
    @DisplayName("findTasks - filtrar por status")
    void whenFilterByStatus_thenReturnOnlyThatStatus() {
        repository.save(build("a", 1L, TaskStatus.PENDENTE, LocalDateTime.now().minusDays(1)));
        repository.save(build("b", 2L, TaskStatus.CONCLUIDO, LocalDateTime.now()));

        var pendentes = repository.findTasks(TaskStatus.PENDENTE, null);
        assertEquals(1, pendentes.size());
        assertEquals(TaskStatus.PENDENTE, pendentes.get(0).getStatus());
    }

    @Test
    @DisplayName("findTasks - filtrar por userId")
    void whenFilterByUserId_thenReturnOnlyThatUser() {
        repository.save(build("a", 5L, TaskStatus.PENDENTE, LocalDateTime.now().minusDays(1)));
        repository.save(build("b", 6L, TaskStatus.PENDENTE, LocalDateTime.now()));

        var user5 = repository.findTasks(null, 5L);
        assertEquals(1, user5.size());
        assertEquals(5L, user5.get(0).getUserId());
    }

    @Test
    @DisplayName("findTasks - filtrar por status e userId")
    void whenFilterByBoth_thenReturnOnlyMatching() {
        repository.save(build("match", 9L, TaskStatus.CONCLUIDO, LocalDateTime.now()));
        repository.save(build("nm1", 9L, TaskStatus.PENDENTE, LocalDateTime.now().minusHours(1)));
        repository.save(build("nm2", 8L, TaskStatus.CONCLUIDO, LocalDateTime.now().minusDays(1)));

        var result = repository.findTasks(TaskStatus.CONCLUIDO, 9L);
        assertEquals(1, result.size());
        assertEquals("match", result.get(0).getTitle());
    }
}