package com.andersonsilva.userservice.adapter.outbound.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "task-service", url = "http://task-service:8080")
public interface TaskClient {

    @GetMapping("/api/tasks/count/by-user/{userId}")
    long countTasksByUserId(@PathVariable("userId") Long userId);

}