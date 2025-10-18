package com.haiph.kongtutorial.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TracingAspect {

    private final Tracer tracer;

    @Around("@annotation(traced)")
    public Object trace(ProceedingJoinPoint joinPoint, Traced traced) throws Throwable {
        String spanName = traced.value().isEmpty()
                ? joinPoint.getSignature().getName()
                : traced.value();

        Span span = tracer.spanBuilder(spanName).startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Add attributes
            span.setAttribute("class", joinPoint.getTarget().getClass().getSimpleName());
            span.setAttribute("method", joinPoint.getSignature().getName());

            // Execute method
            Object result = joinPoint.proceed();

            span.setAttribute("result", result != null ? result.toString() : "null");
            return result;

        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }
}

// Custom annotation
