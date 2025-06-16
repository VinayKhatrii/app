package com.book_library.app.core.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.book_library.app.core.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom entry point that handles authentication failures by returning a JSON response.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        String message;

        // Check for specific exceptions and customize the message
        if (authException instanceof DisabledException) {
            message = "User account is disabled. Please contact support.";
        } else {
            message = authException.getMessage() != null ? authException.getMessage() : "Unauthorized access. Please log in.";
        }

        ApiResponse<Object> apiResponse = new ApiResponse<>(false, message);
        String json = new ObjectMapper().writeValueAsString(apiResponse);
        response.getWriter().write(json);
    }
}
