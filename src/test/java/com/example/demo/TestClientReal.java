package com.example.demo;

import com.example.demo.metrics.MetricsService;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.metrics.TagsEnum.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpStatusClass.CLIENT_ERROR;
import static io.netty.handler.codec.http.HttpStatusClass.SUCCESS;

@SpringBootTest( classes = TestClientReal.FeignConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestClientReal {

    public static WireMockServer wireMockServer;

    @Autowired
    public TestClient client;

    @Autowired
    public MeterRegistry meterRegistry;

    @BeforeAll
    public void setup(){
        wireMockServer = new WireMockServer(options().port(8090));
        wireMockServer.start();
    }

    @Test
    public void shouldRegisterHttpFeignRequestWhenCallFeignClient() {

        wireMockServer.stubFor(get(urlPathMatching("/joke/([a-zA-Z0-9-]*)"))
                .willReturn(aResponse().withStatus(200)));

        client.getJoke("Any");

        List<Meter> counts = meterRegistry.getMeters().stream()
                .filter(it -> it.getId().getName().equals("http.server.requests.feign"))
                .collect(Collectors.toList());

        Assertions.assertTrue(counts.size() != 0);
        Meter counter = counts.get(0);
        Assertions.assertEquals(1.0, ((PrometheusCounter) counter).count());
        Assertions.assertEquals("200", counter.getId().getTag(STATUS.getValue()));
        Assertions.assertEquals("/joke/{name}", counter.getId().getTag(URI.getValue()));
        Assertions.assertEquals(SUCCESS.toString(), counter.getId().getTag(OUTCOME.getValue()));
        Assertions.assertEquals(GET.toString(), counter.getId().getTag(METHOD.getValue()));
        Assertions.assertEquals("None", counter.getId().getTag(EXCEPTION.getValue()));
    }

    @Test
    public void shouldRegisterHttpFeignRequestWithErrorWhenCallFeignClient() {

        wireMockServer.stubFor(get(urlPathMatching("/joke/([a-zA-Z0-9-]*)"))
                .willReturn(aResponse().withStatus(404)));

        try {
            client.getJoke("VeryBad");
        } catch (RuntimeException e) {
            //do nothing
        }

        List<Meter> counts = meterRegistry.getMeters().stream()
                .filter(it -> it.getId().getName().equals("http.server.requests.feign"))
                .collect(Collectors.toList());

        Assertions.assertTrue(counts.size() != 0);
        Meter counter = counts.get(0);
        Assertions.assertEquals(1.0, ((PrometheusCounter) counter).count());
        Assertions.assertEquals("404", counter.getId().getTag(STATUS.getValue()));
        Assertions.assertEquals("/joke/{name}", counter.getId().getTag(URI.getValue()));
        Assertions.assertEquals(CLIENT_ERROR.toString(), counter.getId().getTag(OUTCOME.getValue()));
        Assertions.assertEquals(GET.toString(), counter.getId().getTag(METHOD.getValue()));
        Assertions.assertEquals("None", counter.getId().getTag(EXCEPTION.getValue()));
    }


    @EnableFeignClients(clients = TestClient.class)
    @Configuration
    @EnableAutoConfiguration
    public static class FeignConfig {

        @Autowired
        public MeterRegistry meterRegistry;

        @Bean
        public MetricsService metricsService() {
            return new MetricsService(meterRegistry);
        }
    }
}
