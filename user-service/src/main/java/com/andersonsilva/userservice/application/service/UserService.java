package com.andersonsilva.userservice.application.service;

import com.andersonsilva.userservice.adapter.outbound.persistence.UserRepository;
import com.andersonsilva.userservice.domain.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public List<UserEntity> findAll() {
        return repository.findAll();
    }

    public UserEntity findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UserEntity createUser(String name, String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        UserEntity user = UserEntity.builder()
                .name(name)
                .email(email)
                .build();

        return repository.save(user);
    }

    @Transactional
    public UserEntity updateUser(Long id, String name, String email) {
        UserEntity user = findById(id);

        if (!user.getEmail().equals(email) && repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        user.setName(name);
        user.setEmail(email);

        return repository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = findById(id);
        // TODO: checar tarefas associadas antes de deletar
        repository.delete(user);
    }

}
