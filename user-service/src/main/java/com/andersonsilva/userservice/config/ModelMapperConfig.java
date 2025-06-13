package com.andersonsilva.userservice.config;

import com.andersonsilva.userservice.application.dto.UserResponseDTO;
import com.andersonsilva.userservice.domain.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(UserEntity.class, UserResponseDTO.class)
                .setProvider(request -> {
                    UserEntity user = (UserEntity) request.getSource();
                    return new UserResponseDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getCreatedAt()
                    );
                });

        return modelMapper;
    }

}
