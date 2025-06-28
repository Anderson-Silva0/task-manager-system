package com.andersonsilva.userservice.adapter.inbound.controller;

import com.andersonsilva.userservice.application.dto.UserRequestDTO;
import com.andersonsilva.userservice.application.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserController {

    ResponseEntity<List<UserResponseDTO>> getAll();

    ResponseEntity<UserResponseDTO> getById(Long id);

    ResponseEntity<UserResponseDTO> create(UserRequestDTO dto);

    ResponseEntity<UserResponseDTO> update(Long id, UserRequestDTO dto);

    ResponseEntity<Void> delete(Long id);

}