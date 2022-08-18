package com.example.demo.metrics;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.WeakHashMap;

import static com.example.demo.metrics.TagsEnum.*;

@Component
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private static final String NAME = "http.server.requests.feign";
    private static final String DESCRIPTION = "Duration of HTTP server feign request handling";
    @Getter
    private final Map<String, String> uris = new WeakHashMap<>();

    public Counter getCounter(String exception, String uri, String httpMethod, int statuscode, String outcome) {
       return Counter.builder(NAME)
                .tag(EXCEPTION.getValue(), exception)
                .tag(METHOD.getValue(), httpMethod)
                .tag(OUTCOME.getValue(), outcome)
                .tag(STATUS.getValue(), String.valueOf(statuscode))
                .tag(URI.getValue(), uri)
                .description(DESCRIPTION)
                .register(meterRegistry);
    }
}

