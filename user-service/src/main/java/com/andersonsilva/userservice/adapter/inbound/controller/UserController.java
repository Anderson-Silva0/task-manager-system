package com.andersonsilva.userservice.adapter.inbound.controller;

import com.andersonsilva.userservice.application.dto.UserRequestDTO;
import com.andersonsilva.userservice.application.dto.UserResponseDTO;
import com.andersonsilva.userservice.application.service.UserService;
import com.andersonsilva.userservice.domain.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "APIs para gerenciamento de usuários")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final ModelMapper mapper;

    public UserController(UserService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna uma lista de todos os usuários cadastrados no sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> list = service.findAll()
                .stream()
                .map(user -> mapper.map(user, UserResponseDTO.class))
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "Obter usuário por ID",
            description = "Retorna os dados de um único usuário identificado pelo seu ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long id
    ) {
        UserEntity user = service.findById(id);
        return ResponseEntity.ok(mapper.map(user, UserResponseDTO.class));
    }

    @Operation(
            summary = "Criar novo usuário",
            description = "Registra um usuário com nome e email únicos",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou email duplicado")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Conteúdo do usuário a ser criado", required = true
    )
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserRequestDTO dto
    ) {
        UserEntity created = service.createUser(dto.name(), dto.email());
        return ResponseEntity.status(201).body(mapper.map(created, UserResponseDTO.class));
    }

    @Operation(
            summary = "Atualizar usuário existente",
            description = "Atualiza nome e/ou email de um usuário já cadastrado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou email duplicado"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados do usuário", required = true
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @Parameter(description = "ID do usuário a ser atualizado", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto
    ) {
        UserEntity updated = service.updateUser(id, dto.name(), dto.email());
        return ResponseEntity.ok(mapper.map(updated, UserResponseDTO.class));
    }

    @Operation(
            summary = "Excluir usuário",
            description = "Remove um usuário do sistema, se não houver tarefas associadas",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "400", description = "Não é possível excluir usuário com tarefas associadas")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário a ser excluído", required = true)
            @PathVariable Long id
    ) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
