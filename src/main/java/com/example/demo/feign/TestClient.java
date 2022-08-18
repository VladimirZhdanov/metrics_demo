package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "test",
        url = "https://v2.jokeapi.dev",
        configuration = ClientConfig.class
)
public interface TestClient {
    @GetMapping("/joke/{name}")
    String getJoke(@PathVariable(value="name") String name);

    @PostMapping("/joke/{name}")
    String getJokeBadMethod(@PathVariable(value="name") String name);
}
