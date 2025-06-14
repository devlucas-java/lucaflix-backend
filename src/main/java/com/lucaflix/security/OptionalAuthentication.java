package com.lucaflix.security;

import java.lang.annotation.*;

/**
 * Anotação para endpoints que suportam autenticação opcional.
 *
 * Comportamento:
 * - Se o token JWT estiver presente e válido: usuário será autenticado
 * - Se o token JWT não estiver presente ou for inválido: request continua sem autenticação
 * - Nunca retorna 401 Unauthorized
 * - @CurrentUser funcionará normalmente quando houver token válido
 * - @CurrentUser será null quando não houver token válido
 *
 * Exemplo de uso:
 * @GetMapping("/movies")
 * @OptionalAuthentication
 * public ResponseEntity<?> getMovies(@CurrentUser UserDetails user) {
 *     if (user != null) {
 *         // Usuário logado - retorna dados personalizados
 *         return ResponseEntity.ok(getPersonalizedMovies(user));
 *     } else {
 *         // Usuário não logado - retorna dados básicos
 *         return ResponseEntity.ok(getBasicMovies());
 *     }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptionalAuthentication {
}