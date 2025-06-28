package com.andersonsilva.taskservice.adapter.inbound.controller;

import com.andersonsilva.taskservice.application.dto.TaskRequestDTO;
import com.andersonsilva.taskservice.application.dto.TaskResponseDTO;
import com.andersonsilva.taskservice.domain.TaskStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITaskController {

    ResponseEntity<List<TaskResponseDTO>> getTasks(TaskStatus status, Long userId);

    ResponseEntity<TaskResponseDTO> getTaskById(Long id);

    ResponseEntity<TaskResponseDTO> createTask(TaskRequestDTO dto);

    ResponseEntity<TaskResponseDTO> updateTask(Long id, TaskRequestDTO dto);

    ResponseEntity<Void> deleteTask(Long id);

    ResponseEntity<Long> countByUserId(Long userId);

}
