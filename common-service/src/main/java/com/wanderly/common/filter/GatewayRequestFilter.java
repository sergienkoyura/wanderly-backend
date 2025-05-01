package com.wanderly.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderly.common.util.ResponseFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class GatewayRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getHeader("X-Gateway-Request") == null) {
            // Instead of throwing, write response manually
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");

            String body = new ObjectMapper().writeValueAsString(
                    ResponseFactory.error("You cannot access this resource", null)
            );

            response.getWriter().write(body);
            return;
        }

        filterChain.doFilter(request, response);
    }
}