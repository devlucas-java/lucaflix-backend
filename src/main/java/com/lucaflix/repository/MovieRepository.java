package com.lucaflix.repository;

import com.lucaflix.model.Movie;
import com.lucaflix.model.enums.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Top 10 mais curtidos
    @Query("SELECT m FROM Movie m LEFT JOIN m.likes l GROUP BY m.id ORDER BY COUNT(l) DESC")
    List<Movie> findTop10ByLikes(Pageable pageable);

    // Busca com filtros - Case-insensitive
    @Query("SELECT m FROM Movie m WHERE " +
            "(:title IS NULL OR :title = '' OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:avaliacao IS NULL OR m.avaliacao >= :avaliacao) AND " +
            "(:categoria IS NULL OR :categoria MEMBER OF m.categoria)")
    Page<Movie> buscarPorFiltros(
            @Param("title") String title,
            @Param("avaliacao") Double avaliacao,
            @Param("categoria") Categories categories,
            Pageable pageable);

    // Filmes com avaliação alta
    Page<Movie> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Por categoria
    @Query("SELECT m FROM Movie m WHERE :categoria MEMBER OF m.categoria")
    Page<Movie> findByCategoria(@Param("categoria") Categories categories, Pageable pageable);

    // Filmes populares (mais curtidos)
    @Query("SELECT m FROM Movie m LEFT JOIN m.likes l GROUP BY m ORDER BY COUNT(l) DESC")
    Page<Movie> findPopularMovies(Pageable pageable);

    // Recomendações baseadas nas categorias que o usuário mais curte
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categoria cat WHERE cat IN " +
            "(SELECT DISTINCT c FROM Movie movie JOIN movie.categoria c JOIN movie.likes l WHERE l.user.id = :userId) " +
            "AND m.id NOT IN (SELECT l2.movie.id FROM Like l2 WHERE l2.user.id = :userId AND l2.movie IS NOT NULL) " +
            "ORDER BY m.avaliacao DESC")
    Page<Movie> findRecommendations(@Param("userId") UUID userId, Pageable pageable);

    // Filmes similares (categorias em comum, excluindo o atual)
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categoria cat WHERE cat IN :categorias AND m.id != :excludeId")
    Page<Movie> findSimilarMovies(
            @Param("categorias") List<Categories> categories,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // Busca uma única mídia por título e ano exatos - Case-insensitive
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) = LOWER(:title) AND m.anoLancamento = :year")
    Optional<Movie> findByTitleAndYear(@Param("title") String title, @Param("year") Integer year);

    // Para sitemap
    @Query("SELECT m FROM Movie m WHERE m.title IS NOT NULL AND m.title != ''")
    List<Movie> findAllForSitemap();

    // Avaliação média
    @Query("SELECT AVG(m.avaliacao) FROM Movie m WHERE m.avaliacao IS NOT NULL")
    Double getAverageRating();

    // Contagem por faixas de avaliação
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.avaliacao >= :rating")
    Long countByAvaliacaoGreaterThanEqual(@Param("rating") Double rating);

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.avaliacao BETWEEN :minRating AND :maxRating")
    Long countByAvaliacaoBetween(@Param("minRating") Double minRating, @Param("maxRating") Double maxRating);

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.avaliacao < :rating")
    Long countByAvaliacaoLessThan(@Param("rating") Double rating);

    // Busca principal para SearchService - Case-insensitive
    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.categoria c " +
            "WHERE (:texto IS NULL OR :texto = '' OR " +
            "       LOWER(m.title) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "       LOWER(m.sinopse) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
            "AND (:categoria IS NULL OR c = :categoria) " +
            "ORDER BY m.dataCadastro DESC")
    Page<Movie> searchMovies(@Param("texto") String texto,
                             @Param("categoria") Categories categories,
                             Pageable pageable);
}