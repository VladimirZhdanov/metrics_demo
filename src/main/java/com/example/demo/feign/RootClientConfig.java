package com.example.demo.feign;

import com.example.demo.metrics.MetricsService;
import feign.Client;
import feign.MethodMetadata;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RootClientConfig {
    @Bean
    public Client feignClient(@Autowired MetricsService metricsService) {
        return new CustomFeignClient(null, null, metricsService);
    }

    // Custom handler for exceptions
    @Bean
    public ErrorDecoder errorDecoder(@Autowired MetricsService metricsService) {
        return new CustomErrorDecoder(metricsService);
    }

    @Bean
    public RequestInterceptor requestInterceptor(@Autowired MetricsService metricsService) {
        return requestTemplate -> {
            //com.example.demo.TestClient
            String className = Optional.ofNullable(requestTemplate)
                    .map(RequestTemplate::methodMetadata)
                    .map(MethodMetadata::targetType)
                    .map(Class::getName).orElse(StringUtils.EMPTY);
            //getJoke
            String methodName = Optional.ofNullable(requestTemplate)
                    .map(RequestTemplate::methodMetadata)
                    .map(MethodMetadata::method)
                    .map(Method::getName)
                    .orElse(StringUtils.EMPTY);

            String uri = metricsService.getUris().computeIfAbsent(String.format("%s#%s", className, methodName), newValue -> {
                try {
                    List<String> uris = Arrays.stream(Class.forName(className).getDeclaredMethods())
                            .filter(method -> method.getName().equals(methodName))
                            .map(Method::getDeclaredAnnotations)
                            .flatMap(Arrays::stream)
                            .filter(annotation -> annotation.annotationType().getName().endsWith("Mapping"))
                            .map(annotation -> AnnotationUtils.getAnnotationAttributes(annotation).get("value"))
                            .filter(it -> it instanceof String[])
                            .map(it -> (String[]) it)
                            .flatMap(Arrays::stream)
                            .collect(Collectors.toList());

                    if (uris.size() == 1) {
                        return uris.get(0);
                    } else {
                        //TODO handle
                    }
                } catch (ClassNotFoundException e) {
                    //TODO log
                }
                return StringUtils.EMPTY;
            });
            Optional.ofNullable(requestTemplate).ifPresent(it -> it.header("uri", uri));
        };
    }
}
