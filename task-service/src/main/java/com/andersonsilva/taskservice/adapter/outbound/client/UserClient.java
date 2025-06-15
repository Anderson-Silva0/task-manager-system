package com.andersonsilva.taskservice.adapter.outbound.client;

import com.andersonsilva.taskservice.application.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://user-service:8080")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserDTO findById(@PathVariable("id") Long id);

}
