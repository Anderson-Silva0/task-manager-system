package com.andersonsilva.taskservice.adapter.inbound.controller.impl;

import com.andersonsilva.taskservice.adapter.inbound.controller.ITaskController;
import com.andersonsilva.taskservice.application.dto.TaskRequestDTO;
import com.andersonsilva.taskservice.application.dto.TaskResponseDTO;
import com.andersonsilva.taskservice.application.service.ITaskService;
import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskControllerImpl implements ITaskController {

    @Autowired
    private ITaskService service;

    @Autowired
    private ModelMapper mapper;

    @Override
    public ResponseEntity<List<TaskResponseDTO>> getTasks(TaskStatus status, Long userId) {
        List<TaskResponseDTO> tasks = service.findTasksByStatusAndUserId(status, userId)
                .stream()
                .map(task -> mapper.map(task, TaskResponseDTO.class))
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @Override
    public ResponseEntity<TaskResponseDTO> getTaskById(Long id) {
        TaskEntity task = service.findById(id);
        return ResponseEntity.ok(mapper.map(task, TaskResponseDTO.class));
    }

    @Override
    public ResponseEntity<TaskResponseDTO> createTask(TaskRequestDTO dto) {
        TaskEntity created = service.createTask(
                dto.userId(),
                dto.title(),
                dto.description(),
                dto.deadline()
        );
        TaskResponseDTO response = mapper.map(created, TaskResponseDTO.class);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<TaskResponseDTO> updateTask(Long id, TaskRequestDTO dto) {
        TaskEntity updated = service.updateTask(
                id,
                dto.title(),
                dto.description(),
                dto.status(),
                dto.deadline()
        );
        TaskResponseDTO response = mapper.map(updated, TaskResponseDTO.class);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTask(Long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Long> countByUserId(Long userId) {
        long count = service.countTasksByUserId(userId);
        return ResponseEntity.ok(count);
    }

}
