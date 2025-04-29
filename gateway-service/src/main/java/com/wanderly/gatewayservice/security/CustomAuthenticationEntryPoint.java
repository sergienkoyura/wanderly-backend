package com.wanderly.gatewayservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.util.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = "Invalid or expired token";

        CustomResponse<String> response = ResponseFactory.errorJwt(message, null);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] body = objectMapper.writeValueAsBytes(response);

            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(body);

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
