package com.andersonsilva.taskservice.adapter.inbound.controller.impl;

import com.andersonsilva.taskservice.adapter.inbound.controller.ITaskController;
import com.andersonsilva.taskservice.application.dto.TaskRequestDTO;
import com.andersonsilva.taskservice.application.dto.TaskResponseDTO;
import com.andersonsilva.taskservice.application.service.ITaskService;
import com.andersonsilva.taskservice.domain.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tasks", description = "APIs para gerenciamento de tarefas")
@RestController
@RequestMapping("/api/tasks")
public class TaskControllerImpl implements ITaskController {

    private final ITaskService service;
    private final ModelMapper mapper;

    public TaskControllerImpl(ITaskService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @Operation(
            summary = "Listar tarefas",
            description = "Retorna todas as tarefas, com filtros opcionais por status e usuário responsável",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(
            @Parameter(description = "Status da tarefa: PENDENTE, EM_ANDAMENTO ou CONCLUIDO")
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "ID do usuário responsável pelas tarefas")
            @RequestParam(required = false) Long userId
    ) {
        List<TaskResponseDTO> tasks = service.findTasksByStatusAndUserId(status, userId)
                .stream()
                .map(task -> mapper.map(task, TaskResponseDTO.class))
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @Override
    @Operation(
            summary = "Obter tarefa por ID",
            description = "Busca uma tarefa específica pelo seu identificador",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @Parameter(description = "ID da tarefa", required = true)
            @PathVariable Long id
    ) {
        TaskResponseDTO dto = mapper.map(service.findById(id), TaskResponseDTO.class);
        return ResponseEntity.ok(dto);
    }

    @Override
    @Operation(
            summary = "Criar nova tarefa",
            description = "Registra uma nova tarefa associada a um usuário existente",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou usuário não existe")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados para criação da tarefa", required = true
    )
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO dto
    ) {
        TaskResponseDTO response = mapper.map(
                service.createTask(
                        dto.userId(), dto.title(), dto.description(), dto.deadline()
                ), TaskResponseDTO.class
        );
        return ResponseEntity.status(201).body(response);
    }

    @Override
    @Operation(
            summary = "Atualizar tarefa",
            description = "Altera os dados de uma tarefa existente, exceto se estiver concluída",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou tentativa de editar tarefa concluída"),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados da tarefa", required = true
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "ID da tarefa a ser atualizada", required = true)
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO dto
    ) {
        TaskResponseDTO response = mapper.map(
                service.updateTask(
                        id, dto.title(), dto.description(), dto.status(), dto.deadline()
                ), TaskResponseDTO.class
        );
        return ResponseEntity.ok(response);
    }

    @Override
    @Operation(
            summary = "Remover tarefa",
            description = "Exclui a tarefa identificada pelo ID (sem corpo de resposta)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tarefa removida com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID da tarefa a ser removida", required = true)
            @PathVariable Long id
    ) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @Operation(
            summary = "Contar tarefas por usuário",
            description = "Retorna a quantidade de tarefas associadas a um determinado usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contagem de tarefas retornada com sucesso")
            }
    )
    @GetMapping("/count/by-user/{userId}")
    public ResponseEntity<Long> countByUserId(
            @Parameter(description = "ID do usuário para contagem", required = true)
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(service.countTasksByUserId(userId));
    }

}
