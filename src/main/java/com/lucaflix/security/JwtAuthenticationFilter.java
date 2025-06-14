package com.lucaflix.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.lucaflix.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        logger.debug("Checking if path should be filtered: {}", path);

        // Ignore endpoints that don't require authentication (seus endpoints fixos)
        if (path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/movies/top10") ||
                path.startsWith("/api/sitemap.xml") ||
                path.startsWith("/api/sitemap/urls") ||
                path.contains("/api/payments/webhook/stripe") ||
                path.contains("/webhook/stripe") ||
                "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Verificar se o endpoint tem a anotação @SkipJwtAuthentication
        try {
            HandlerExecutionChain handlerChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerChain != null && handlerChain.getHandler() instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handlerChain.getHandler();

                // Verificar se o método tem a anotação
                if (handlerMethod.hasMethodAnnotation(SkipJwtAuthentication.class)) {
                    logger.debug("Skipping JWT authentication for method: {}", handlerMethod.getMethod().getName());
                    return true;
                }

                // Verificar se a classe do controller tem a anotação
                if (handlerMethod.getBeanType().isAnnotationPresent(SkipJwtAuthentication.class)) {
                    logger.debug("Skipping JWT authentication for class: {}", handlerMethod.getBeanType().getSimpleName());
                    return true;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not determine handler for request: {}", e.getMessage());
            // Se não conseguir determinar o handler, continue com a autenticação normal
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        boolean isOptionalAuth = isOptionalAuthentication(request);

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                String roles = tokenProvider.getRolesFromJWT(jwt);

                // Check if user still exists in database
                UserDetails userDetails;
                try {
                    userDetails = userDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException ex) {
                    logger.warn("User not found in database: {}", username);
                    // Para optional auth, continua sem autenticação em vez de falhar
                    if (isOptionalAuth) {
                        logger.debug("Optional auth: continuing without authentication for missing user: {}", username);
                    }
                    filterChain.doFilter(request, response);
                    return;
                }

                // Validate roles from token
                if (roles == null || roles.trim().isEmpty()) {
                    logger.warn("No roles found in JWT token for user: {}", username);
                    // Para optional auth, continua sem autenticação
                    if (isOptionalAuth) {
                        logger.debug("Optional auth: continuing without authentication for missing roles: {}", username);
                    }
                    filterChain.doFilter(request, response);
                    return;
                }

                Collection<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                        .filter(role -> !role.trim().isEmpty()) // Filter out empty roles
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Successfully authenticated user: {}", username);
            } else if (isOptionalAuth) {
                // Para optional auth, se não há token válido, continua sem autenticação
                logger.debug("Optional auth: no valid token found, continuing without authentication");
            }
        } catch (Exception ex) {
            if (isOptionalAuth) {
                logger.debug("Optional auth: authentication failed, continuing without authentication: {}", ex.getMessage());
                // Para optional auth, limpa o contexto mas continua
                SecurityContextHolder.clearContext();
            } else {
                logger.error("Could not authenticate user: {}", ex.getMessage(), ex);
                // Clear security context to ensure no partial authentication
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Verifica se o endpoint atual tem autenticação opcional
     */
    private boolean isOptionalAuthentication(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerChain != null && handlerChain.getHandler() instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handlerChain.getHandler();

                // Verificar se o método tem a anotação @OptionalAuthentication
                if (handlerMethod.hasMethodAnnotation(OptionalAuthentication.class)) {
                    return true;
                }

                // Verificar se a classe do controller tem a anotação @OptionalAuthentication
                if (handlerMethod.getBeanType().isAnnotationPresent(OptionalAuthentication.class)) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not determine if optional authentication: {}", e.getMessage());
        }
        return false;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}