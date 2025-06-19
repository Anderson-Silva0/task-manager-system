package com.andersonsilva.taskservice.adapter.inbound.controller;

import com.andersonsilva.taskservice.application.dto.TaskRequestDTO;
import com.andersonsilva.taskservice.application.dto.TaskResponseDTO;
import com.andersonsilva.taskservice.domain.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tasks", description = "APIs para gerenciamento de tarefas")
@RequestMapping("/api/tasks")
public interface ITaskController {

    @Operation(
            summary = "Listar tarefas",
            description = "Retorna todas as tarefas, com filtros opcionais por status e usuário responsável",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
            }
    )
    @GetMapping
    ResponseEntity<List<TaskResponseDTO>> getTasks(
            @Parameter(description = "Status da tarefa: PENDENTE, EM_ANDAMENTO ou CONCLUIDO")
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "ID do usuário responsável pelas tarefas")
            @RequestParam(required = false) Long userId
    );

    @Operation(
            summary = "Obter tarefa por ID",
            description = "Busca uma tarefa específica pelo seu identificador",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<TaskResponseDTO> getTaskById(
            @Parameter(description = "ID da tarefa", required = true)
            @PathVariable Long id
    );

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
    ResponseEntity<TaskResponseDTO> createTask(
            @Valid @org.springframework.web.bind.annotation.RequestBody TaskRequestDTO dto
    );

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
    ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "ID da tarefa a ser atualizada", required = true)
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody TaskRequestDTO dto
    );

    @Operation(
            summary = "Remover tarefa",
            description = "Exclui a tarefa identificada pelo ID (sem corpo de resposta)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tarefa removida com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID da tarefa a ser removida", required = true)
            @PathVariable Long id
    );

    @Operation(
            summary = "Contar tarefas por usuário",
            description = "Retorna a quantidade de tarefas associadas a um determinado usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contagem de tarefas retornada com sucesso")
            }
    )
    @GetMapping("/count/by-user/{userId}")
    ResponseEntity<Long> countByUserId(
            @Parameter(description = "ID do usuário para contagem", required = true)
            @PathVariable Long userId
    );

}
