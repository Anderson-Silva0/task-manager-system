package com.andersonsilva.userservice.application.service;

import com.andersonsilva.userservice.domain.UserEntity;

import java.util.List;

public interface IUserService {

    List<UserEntity> findAll();

    UserEntity findById(Long id);

    UserEntity createUser(String name, String email);

    UserEntity updateUser(Long id, String name, String email);

    void deleteUser(Long id);

}
