package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "testTimeOut",
        url = "https://10.255.255.1",
        configuration = RootClientConfig.class
)
public interface TestTimeOutClient {
    @GetMapping("/test/{name}")
    String getTimeOut(@PathVariable(value="name") String name);
}
