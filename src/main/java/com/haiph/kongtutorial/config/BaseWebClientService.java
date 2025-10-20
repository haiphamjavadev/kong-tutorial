package com.haiph.kongtutorial.config;


import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

@Component
public class BaseWebClientService {

    private final WebClient webClient;
    private final Tracer tracer;

    @Autowired
    public BaseWebClientService(WebClient.Builder builder, Tracer tracer) {
        this.tracer = tracer;
        this.webClient = builder
//                .baseUrl("http://localhost:8080") // override khi gọi
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public <T> T get(String url, Class<T> responseType, String token) {
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("trace-id", getCurrentTraceId())
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .block();
    }

    public <T> T post(String url, Object body, Class<T> responseType, String token) {
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("trace-id", getCurrentTraceId())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .block();
    }

    private String getCurrentTraceId() {
        if (tracer != null && tracer.currentSpan() != null) {
            return Objects.requireNonNull(tracer.currentSpan()).context().traceId();
        }
        return "no-trace";
    }
}
