package com.wanderly.gatewayservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderly.common.dto.CustomResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.AuthenticationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CustomAuthenticationEntryPointTest {

    private ObjectMapper objectMapper;
    private CustomAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        entryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Test
    void commence_ReturnsUnauthorizedJsonResponse() throws Exception {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        AuthenticationException exception = mock(AuthenticationException.class);

        // Act
        Mono<Void> result = entryPoint.commence(exchange, exception);

        StepVerifier.create(result).expectComplete().verify();
        MockServerHttpResponse response = exchange.getResponse();

        // Body
        Flux<DataBuffer> body = response.getBody();
        String responseJson = body
                .map(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .reduce(String::concat)
                .block();

        CustomResponse<?> actualResponse = objectMapper.readValue(responseJson, CustomResponse.class);

        assertThat(actualResponse.getMessage()).isEqualTo("Invalid or expired token");
        assertThat(actualResponse.getStatus()).isEqualTo("error-jwt");

        // Status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // Content-Type
        HttpHeaders headers = response.getHeaders();
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }
}
