//package com.lucaflix.repository;
//
//import com.lucaflix.model.Anime;
//import com.lucaflix.model.enums.Categoria;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface AnimeRepository extends JpaRepository<Anime, Long> {
//
//    // Top 10 mais curtidos
//    @Query("SELECT a FROM Anime a LEFT JOIN a.likes l GROUP BY a.id ORDER BY COUNT(l) DESC")
//    List<Anime> findTop10ByLikes(Pageable pageable);
//
//    // Busca com filtros - ATUALIZADA para case-insensitive
//    @Query("SELECT a FROM Anime a WHERE " +
//            "(:title IS NULL OR :title = '' OR LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
//            "(:avaliacao IS NULL OR a.avaliacao >= :avaliacao) AND " +
//            "(:categoria IS NULL OR :categoria MEMBER OF a.categoria)")
//    Page<Anime> buscarPorFiltros(
//            @Param("title") String title,
//            @Param("avaliacao") Double avaliacao,
//            @Param("categoria") Categoria categoria,
//            Pageable pageable);
//
//    // Animes com avaliação alta
//    Page<Anime> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);
//
//    // Por categoria
//    @Query("SELECT a FROM Anime a WHERE :categoria MEMBER OF a.categoria")
//    Page<Anime> findByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);
//
//    // Animes populares (mais curtidos)
//    @Query("SELECT a FROM Anime a LEFT JOIN a.likes l GROUP BY a ORDER BY COUNT(l) DESC")
//    Page<Anime> findPopularAnimes(Pageable pageable);
//
//    // Por ano
//    @Query("SELECT a FROM Anime a WHERE EXTRACT(YEAR FROM a.anoLancamento) = :year")
//    Page<Anime> findByYear(@Param("year") Integer year, Pageable pageable);
//
//    // Recomendações baseadas nas categorias que o usuário mais curte
//    @Query("SELECT DISTINCT a FROM Anime a JOIN a.categoria cat WHERE cat IN " +
//            "(SELECT DISTINCT c FROM Anime anime JOIN anime.categoria c JOIN anime.likes l WHERE l.user.id = :userId) " +
//            "AND a.id NOT IN (SELECT l2.movie.id FROM Like l2 WHERE l2.user.id = :userId AND l2.movie IS NOT NULL) " +
//            "ORDER BY a.avaliacao DESC")
//    Page<Anime> findRecommendations(@Param("userId") UUID userId, Pageable pageable);
//
//    // Animes similares (categorias em comum, excluindo o atual)
//    @Query("SELECT DISTINCT a FROM Anime a JOIN a.categoria cat WHERE cat IN :categorias AND a.id != :excludeId")
//    Page<Anime> findSimilarAnimes(
//            @Param("categorias") List<Categoria> categorias,
//            @Param("excludeId") Long excludeId,
//            Pageable pageable);
//
//    // Busca uma única mídia por título e ano exatos - ATUALIZADA para case-insensitive
//    @Query("SELECT a FROM Anime a WHERE LOWER(a.title) = LOWER(:title) AND EXTRACT(YEAR FROM a.anoLancamento) = :year")
//    Optional<Anime> findByTitleAndYear(@Param("title") String title, @Param("year") Integer year);
//
//    // Para sitemap
//    @Query("SELECT a FROM Anime a WHERE a.title IS NOT NULL AND a.title != ''")
//    List<Anime> findAllForSitemap();
//
//    // Estatísticas de contagem por categoria
//    @Query("SELECT cat, COUNT(a) FROM Anime a JOIN a.categoria cat GROUP BY cat")
//    List<Object[]> countByCategoria();
//
//    // Contagem por ano
//    @Query("SELECT EXTRACT(YEAR FROM a.anoLancamento) as year, COUNT(a) FROM Anime a GROUP BY EXTRACT(YEAR FROM a.anoLancamento) ORDER BY year DESC")
//    List<Object[]> countByYear();
//
//    // Avaliação média
//    @Query("SELECT AVG(a.avaliacao) FROM Anime a WHERE a.avaliacao IS NOT NULL")
//    Double getAverageRating();
//
//    // Contagem por faixas de avaliação
//    @Query("SELECT COUNT(a) FROM Anime a WHERE a.avaliacao >= :rating")
//    Long countByAvaliacaoGreaterThanEqual(@Param("rating") Double rating);
//
//    @Query("SELECT COUNT(a) FROM Anime a WHERE a.avaliacao BETWEEN :minRating AND :maxRating")
//    Long countByAvaliacaoBetween(@Param("minRating") Double minRating, @Param("maxRating") Double maxRating);
//
//    @Query("SELECT COUNT(a) FROM Anime a WHERE a.avaliacao < :rating")
//    Long countByAvaliacaoLessThan(@Param("rating") Double rating);
//
//    // Busca de animes para o SearchService - ATUALIZADA para case-insensitive
//    @Query("SELECT DISTINCT a FROM Anime a " +
//            "LEFT JOIN a.categoria c " +
//            "WHERE (:texto IS NULL OR :texto = '' OR " +
//            "       LOWER(a.title) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
//            "       LOWER(a.sinopse) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
//            "AND (:categoria IS NULL OR c = :categoria) " +
//            "ORDER BY a.dataCadastro DESC")
//    Page<Anime> searchAnimes(@Param("texto") String texto,
//                             @Param("categoria") Categoria categoria,
//                             Pageable pageable);
//}