package com.andersonsilva.taskservice.application.service.impl;

import com.andersonsilva.taskservice.adapter.outbound.client.UserClient;
import com.andersonsilva.taskservice.adapter.outbound.persistence.TaskRepository;
import com.andersonsilva.taskservice.application.service.ITaskService;
import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository repository;

    private final UserClient userClient;

    public TaskServiceImpl(TaskRepository repository, UserClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
    }

    @Override
    public List<TaskEntity> findTasksByStatusAndUserId(TaskStatus status, Long userId) {
        if (status != null && userId != null) {
            return repository.findByStatusAndUserId(status, userId);
        } else if (status != null) {
            return repository.findByStatus(status);
        } else if (userId != null) {
            return repository.findByUserId(userId);
        } else {
            return repository.findAll();
        }
    }

    @Override
    public TaskEntity findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
    }

    @Override
    @Transactional
    public TaskEntity createTask(Long userId, String title, String description, LocalDateTime deadline) {
        try {
            userClient.findById(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        TaskEntity task = TaskEntity.builder()
                .userId(userId)
                .title(title)
                .description(description)
                .status(TaskStatus.PENDENTE)
                .deadline(deadline)
                .build();
        return repository.save(task);
    }

    @Override
    @Transactional
    public TaskEntity updateTask(Long id, String title, String description, TaskStatus status, LocalDateTime deadline) {
        TaskEntity task = findById(id);

        if (task.getStatus() == TaskStatus.CONCLUIDO) {
            throw new IllegalStateException("Não é possível editar tarefas concluídas");
        }

        try {
            userClient.findById(task.getUserId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setDeadline(deadline);

        return repository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        TaskEntity task = findById(id);
        repository.delete(task);
    }

    @Override
    public long countTasksByUserId(Long userId) {
        return repository.countByUserId(userId);
    }

}
