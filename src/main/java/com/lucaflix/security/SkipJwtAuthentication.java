package com.lucaflix.security;

import java.lang.annotation.*;

/**
 * Anotação personalizada para pular a autenticação JWT em endpoints específicos.
 * Quando aplicada a um método de controller, o filtro JWT será ignorado para aquele endpoint.
 *
 * Exemplo de uso:
 * @GetMapping("/public-endpoint")
 * @SkipJwtAuthentication
 * public ResponseEntity<String> publicEndpoint() {
 *     return ResponseEntity.ok("Este endpoint não requer autenticação");
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipJwtAuthentication {
}