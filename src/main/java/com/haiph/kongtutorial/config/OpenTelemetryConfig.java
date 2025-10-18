package com.haiph.kongtutorial.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${otel.exporter.jaeger.endpoint:http://localhost:4317}")
    private String jaegerEndpoint;

    @Bean
    public OpenTelemetry openTelemetry() {
        // Create resource with service name
        Resource resource = Resource.getDefault()
                .merge(Resource.create(
                        Attributes.of(
                                ResourceAttributes.SERVICE_NAME, serviceName,
                                ResourceAttributes.SERVICE_VERSION, "1.0.0"
                        )
                ));

        // Configure OTLP exporter (Jaeger hỗ trợ OTLP)
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(jaegerEndpoint)
                .build();

        // Create tracer provider
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .setResource(resource)
                .setSampler(Sampler.alwaysOn())  // Sample all traces
                .build();

        // Create OpenTelemetry instance
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(
                        W3CTraceContextPropagator.getInstance()
                ))
                .buildAndRegisterGlobal();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));

        return openTelemetry;
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName, "1.0.0");
    }
}