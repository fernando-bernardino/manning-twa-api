package com.twa.flights.api.clusters.connector.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
public class ConnectorFilter {
    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            logResponseStatus(response);
            logResponseHeaders(response);
            return logResponseBody(response);
        });
    }

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Request: {} {}", request.method(), request.url());
            logRequestHeaders(request);
            return Mono.just(request);
        });
    }

    private static void logRequestHeaders(ClientRequest request) {
        String headers = request.headers().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(","));
        log.info("Headers: [{}]", headers);
    }

    private static void logResponseStatus(ClientResponse response) {
        HttpStatus status = response.statusCode();
        log.info("Returned status code {} ({})", status.value(), status.getReasonPhrase());
    }

    private static Mono<ClientResponse> logResponseBody(ClientResponse response) {
        return response.bodyToMono(String.class).flatMap(body -> {
            log.info("Body is {}", body);
            return Mono.just(response);
        });
    }

    private static void logResponseHeaders(ClientResponse response) {
        String headers = response.headers().asHttpHeaders().entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
        log.info("Headers [{}]", headers);
    }
}
