package com.andersonsilva.taskservice.adapter.inbound.controller;

import com.andersonsilva.taskservice.application.dto.TaskRequestDTO;
import com.andersonsilva.taskservice.application.dto.TaskResponseDTO;
import com.andersonsilva.taskservice.application.service.TaskService;
import com.andersonsilva.taskservice.domain.TaskEntity;
import com.andersonsilva.taskservice.domain.TaskStatus;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;
    private final ModelMapper mapper;

    public TaskController(TaskService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long userId) {
        List<TaskResponseDTO> tasks = service.findTasksByStatusAndUserId(status, userId)
                .stream()
                .map(task -> mapper.map(task, TaskResponseDTO.class))
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        TaskEntity task = service.findById(id);
        return ResponseEntity.ok(mapper.map(task, TaskResponseDTO.class));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        TaskEntity created = service.createTask(
                dto.userId(),
                dto.title(),
                dto.description(),
                dto.deadline());
        TaskResponseDTO response = mapper.map(created, TaskResponseDTO.class);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO dto) {
        TaskEntity updated = service.updateTask(
                id,
                dto.title(),
                dto.description(),
                dto.status(),
                dto.deadline());
        TaskResponseDTO response = mapper.map(updated, TaskResponseDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/by-user/{userId}")
    public ResponseEntity<Long> countByUserId(@PathVariable Long userId) {
        long count = service.countTasksByUserId(userId);
        return ResponseEntity.ok(count);
    }

}
