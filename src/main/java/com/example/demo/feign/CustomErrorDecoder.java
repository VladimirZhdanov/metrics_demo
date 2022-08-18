package com.example.demo.feign;

import com.example.demo.metrics.MetricsService;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {

    private final MetricsService metricsService;

    @Override
    public Exception decode(String methodKey, Response response) {
        return new RuntimeException("" + response.status());
    }
}
