package com.andersonsilva.userservice.adapter.inbound.controller.impl;

import com.andersonsilva.userservice.adapter.inbound.controller.IUserController;
import com.andersonsilva.userservice.application.dto.UserRequestDTO;
import com.andersonsilva.userservice.application.dto.UserResponseDTO;
import com.andersonsilva.userservice.application.service.IUserService;
import com.andersonsilva.userservice.domain.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserControllerImpl implements IUserController {

    @Autowired
    private IUserService service;

    @Autowired
    private ModelMapper mapper;

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> list = service.findAll()
                .stream()
                .map(user -> mapper.map(user, UserResponseDTO.class))
                .toList();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<UserResponseDTO> getById(Long id) {
        UserEntity user = service.findById(id);
        return ResponseEntity.ok(mapper.map(user, UserResponseDTO.class));
    }

    @Override
    public ResponseEntity<UserResponseDTO> create(UserRequestDTO dto) {
        UserEntity created = service.createUser(dto.name(), dto.email());
        return ResponseEntity.status(201).body(mapper.map(created, UserResponseDTO.class));
    }

    @Override
    public ResponseEntity<UserResponseDTO> update(Long id, UserRequestDTO dto) {
        UserEntity updated = service.updateUser(id, dto.name(), dto.email());
        return ResponseEntity.ok(mapper.map(updated, UserResponseDTO.class));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
