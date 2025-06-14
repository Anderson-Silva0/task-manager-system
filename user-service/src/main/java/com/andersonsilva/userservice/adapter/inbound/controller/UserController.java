package com.andersonsilva.userservice.adapter.inbound.controller;

import com.andersonsilva.userservice.application.dto.UserRequestDTO;
import com.andersonsilva.userservice.application.dto.UserResponseDTO;
import com.andersonsilva.userservice.application.service.UserService;
import com.andersonsilva.userservice.domain.UserEntity;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserService service;
    private final ModelMapper mapper;

    public UserController(UserService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> userResponseDTOList = service.findAll()
                .stream()
                .map(user -> mapper.map(user, UserResponseDTO.class))
                .toList();
        return ResponseEntity.ok(userResponseDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        UserEntity user = service.findById(id);
        return ResponseEntity.ok(mapper.map(user, UserResponseDTO.class));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        UserEntity user = service.createUser(dto.name(), dto.email());
        return ResponseEntity.ok(mapper.map(user, UserResponseDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        UserEntity user = service.updateUser(id, dto.name(), dto.email());
        return ResponseEntity.ok(mapper.map(user, UserResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
