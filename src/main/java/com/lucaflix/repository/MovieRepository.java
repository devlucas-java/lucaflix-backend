package com.lucaflix.repository;

import com.lucaflix.model.Movie;
import com.lucaflix.model.enums.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Top 10 mais curtidas
    @Query("SELECT m FROM Movie m LEFT JOIN m.likes l GROUP BY m.id ORDER BY COUNT(l) DESC")
    List<Movie> findTop10ByLikes(Pageable pageable);

    // Busca com filtros
    @Query("SELECT m FROM Movie m WHERE " +
            "(:title IS NULL OR UPPER(m.title) LIKE UPPER(CONCAT('%', :title, '%'))) AND " +
            "(:avaliacao IS NULL OR m.avaliacao >= :avaliacao) AND " +
            "(:categoria IS NULL OR :categoria MEMBER OF m.categoria)")
    Page<Movie> buscarPorFiltros(
            @Param("title") String title,
            @Param("avaliacao") Double avaliacao,
            @Param("categoria") Categoria categoria,
            Pageable pageable);

    // Mídias com avaliação alta
    Page<Movie> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Por categoria
    @Query("SELECT m FROM Movie m WHERE :categoria MEMBER OF m.categoria")
    Page<Movie> findByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);

    // Mídias populares (mais curtidas)
    @Query("SELECT m FROM Movie m LEFT JOIN m.likes l GROUP BY m ORDER BY COUNT(l) DESC")
    Page<Movie> findPopularMovies(Pageable pageable);

    // Por ano
    @Query("SELECT m FROM Movie m WHERE EXTRACT(YEAR FROM m.anoLancamento) = :year")
    Page<Movie> findByYear(@Param("year") Integer year, Pageable pageable);

    // Recomendações baseadas nas categorias que o usuário mais curte
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categoria cat WHERE cat IN " +
            "(SELECT DISTINCT c FROM Movie media JOIN media.categoria c JOIN media.likes l WHERE l.user.id = :userId) " +
            "AND m.id NOT IN (SELECT l2.movie.id FROM Like l2 WHERE l2.user.id = :userId AND l2.movie IS NOT NULL) " +
            "ORDER BY m.avaliacao DESC")
    Page<Movie> findRecommendations(@Param("userId") UUID userId, Pageable pageable);

    // Mídias similares (categorias em comum, excluindo a atual)
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categoria cat WHERE cat IN :categorias AND m.id != :excludeId")
    Page<Movie> findSimilarMedia(
            @Param("categorias") List<Categoria> categorias,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // Busca uma única mídia por título e ano exatos
    @Query("SELECT m FROM Movie m WHERE UPPER(m.title) = UPPER(:title) AND EXTRACT(YEAR FROM m.anoLancamento) = :year")
    Optional<Movie> findByTitleAndYear(@Param("title") String title, @Param("year") Integer year);

    // Para sitemap
    @Query("SELECT m FROM Movie m WHERE m.title IS NOT NULL AND m.title != ''")
    List<Movie> findAllForSitemap();



    // Métodos adicionais para MovieRepository.java

    // Estatísticas de contagem por categoria
    @Query("SELECT cat, COUNT(m) FROM Movie m JOIN m.categoria cat GROUP BY cat")
    List<Object[]> countByCategoria();

    // Contagem por ano
    @Query("SELECT EXTRACT(YEAR FROM m.anoLancamento) as year, COUNT(m) FROM Movie m GROUP BY EXTRACT(YEAR FROM m.anoLancamento) ORDER BY year DESC")
    List<Object[]> countByYear();

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
    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.categoria c " +
            "WHERE (:texto IS NULL OR " +
            "       LOWER(m.title) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "       LOWER(m.sinopse) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
            "AND (:categoria IS NULL OR c = :categoria) " +
            "ORDER BY m.dataCadastro DESC")
    Page<Movie> searchMovies(@Param("texto") String texto,
                             @Param("categoria") Categoria categoria,
                             Pageable pageable);

// ====================================================================
}