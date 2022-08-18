package com.example.demo.controllers;

import com.example.demo.feign.TestClient;
import com.example.demo.feign.TestTimeOutClient;
import com.example.demo.model.JokeDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@AllArgsConstructor
public class DemoController {
    private final RestTemplate restTemplate;
    private final TestClient testClient;
    private final TestTimeOutClient testTimeOutClient;

    @GetMapping("/greeting/{name}")
    public String greeting(@PathVariable(value="name") String name) {
        testClient.getJoke("Any");
        return "Hello " + name;
    }

    @GetMapping("/timeout")
    public String timeout() {
        return testTimeOutClient.getTimeOut("timeout");
    }

    @GetMapping("/joks")
    public String joks() {

        String fooResourceUrl
                = "https://v2.jokeapi.dev";
        ResponseEntity<JokeDto> response
                = restTemplate.getForEntity(fooResourceUrl + "/joke/Any", JokeDto.class);
        JokeDto jokeDto = response.getBody();

        return jokeDto.getSetup() + System.lineSeparator() + jokeDto.getDelivery();
    }
}
