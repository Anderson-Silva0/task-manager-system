package com.andersonsilva.taskservice.config;

import com.andersonsilva.taskservice.application.dto.TaskResponseDTO;
import com.andersonsilva.taskservice.domain.TaskEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(TaskEntity.class, TaskResponseDTO.class)
                .setProvider(request -> {
                    TaskEntity task = (TaskEntity) request.getSource();
                    return new TaskResponseDTO(
                            task.getId(),
                            task.getUserId(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getStatus(),
                            task.getCreatedAt(),
                            task.getDeadline()
                    );
                });

        return modelMapper;
    }

}
