package com.example.demo;

import com.example.demo.feign.ClientConfig;
import com.example.demo.feign.RootClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "test",
        url = "127.0.0.1:8090",
        configuration = {ClientConfig.class, RootClientConfig.class}
)
public interface TestClient {
    @GetMapping("/joke/{name}")
    String getJoke(@PathVariable(value="name") String name);

    @PostMapping("/joke/{name}")
    String getJokeWithError(@PathVariable(value="name") String name);
}
