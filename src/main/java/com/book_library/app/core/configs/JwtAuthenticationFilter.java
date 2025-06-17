package  com.book_library.app.core.configs;

import com.book_library.app.core.services.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userService;

    private static final List<String> OPTIONAL_AUTH_ENDPOINTS = List.of(
    
    );
    
    private static final List<Pattern> OPTIONAL_AUTH_PATTERNS = List.of(

    );
    

    public static final List<String> EXCLUDED_ENDPOINTS = List.of(
        "/api/v1/users/login",
        "/api/v1/users/register",
        "/api/v1/books"
    );

    private static final List<Pattern> EXCLUDED_PATTERNS = List.of(
       
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("OPTIONS request ignored: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        if (!uri.contains("/api/") || isExcluded(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (isOptionalAuth(uri)) {
                log.debug("No token provided for optional-auth endpoint: {}", uri);
                filterChain.doFilter(request, response);
                return;
            } else {
                sendUnauthorizedResponse(response, "Unauthorized: Token is missing or invalid");
                return;
            }
        }

        final String jwt = authHeader.substring(7);
        try {
            final String username = jwtUtil.extractUsername(jwt);
            log.debug("Extracted username from token: {}", username);

            if (username == null) {
                sendUnauthorizedResponse(response, "Unauthorized: Token is invalid");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendUnauthorizedResponse(response, "Unauthorized: Token is invalid");
                    return;
                }
            }
        } catch (SignatureException | ExpiredJwtException e) {
            log.error("JWT token error: {}", e.getMessage(), e);
            sendUnauthorizedResponse(response, "Unauthorized: Token is invalid or expired");
            return;
        }

        filterChain.doFilter(request, response);
    }

   
    private boolean isOptionalAuth(String uri) {
        return OPTIONAL_AUTH_ENDPOINTS.stream().anyMatch(uri::endsWith) ||
               OPTIONAL_AUTH_PATTERNS.stream().anyMatch(p -> p.matcher(uri).matches());
    }
    

    private boolean isExcluded(String uri) {
        return EXCLUDED_ENDPOINTS.stream().anyMatch(uri::endsWith) ||
                EXCLUDED_PATTERNS.stream().anyMatch(p -> p.matcher(uri).matches());
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }
}
