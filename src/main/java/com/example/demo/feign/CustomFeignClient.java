package com.example.demo.feign;

import com.example.demo.metrics.MetricsService;
import feign.Client;
import feign.Request;
import feign.Response;
import io.netty.handler.codec.http.HttpStatusClass;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class CustomFeignClient extends Client.Default {

    private final MetricsService metricsService;

    public CustomFeignClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier, MetricsService metricsService) {
        super(sslContextFactory, hostnameVerifier);
        this.metricsService = metricsService;
    }

    @Override
    public Response execute(Request request, Request.Options options) {
        String uri = Optional.ofNullable(request)
                .map(Request::headers)
                .map(it -> it.get("uri"))
                .orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .orElse("");
        String httpMethod = Optional.ofNullable(request)
                .map(Request::httpMethod)
                .map(Objects::toString)
                .orElse("");

        try {
            Response response = super.execute(request, options);
            Optional.ofNullable(metricsService)
                    .ifPresent(it -> metricsService.getCounter("None", uri, httpMethod,
                                    response.status(), HttpStatusClass.valueOf(response.status()).toString())
                            .increment());
            return response;
        } catch (IOException e) {
            Optional.ofNullable(metricsService)
                    .ifPresent(it -> metricsService.getCounter(e.getClass().getSimpleName(), uri, httpMethod,
                                    HttpURLConnection.HTTP_INTERNAL_ERROR, HttpStatusClass.UNKNOWN.toString())
                            .increment());
            return null;
        }
    }
}
