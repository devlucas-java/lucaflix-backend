package com.lucaflix.repository;

import com.lucaflix.model.Serie;
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
public interface SerieRepository extends JpaRepository<Serie, Long> {

    // Top 10 mais curtidas
    @Query("SELECT s FROM Serie s LEFT JOIN s.likes l GROUP BY s.id ORDER BY COUNT(l) DESC")
    List<Serie> findTop10ByLikes(Pageable pageable);

    // Busca com filtros (similar ao MovieRepository)
    @Query("SELECT s FROM Serie s WHERE " +
            "(:title IS NULL OR UPPER(s.title) LIKE UPPER(CONCAT('%', :title, '%'))) AND " +
            "(:avaliacao IS NULL OR s.avaliacao >= :avaliacao) AND " +
            "(:categoria IS NULL OR :categoria MEMBER OF s.categoria)")
    Page<Serie> buscarPorFiltros(
            @Param("title") String title,
            @Param("avaliacao") Double avaliacao,
            @Param("categoria") Categoria categoria,
            Pageable pageable);

    // Por categoria
    @Query("SELECT s FROM Serie s WHERE :categoria MEMBER OF s.categoria")
    Page<Serie> findByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);

    // Séries populares (mais curtidas)
    @Query("SELECT s FROM Serie s LEFT JOIN s.likes l GROUP BY s ORDER BY COUNT(l) DESC")
    Page<Serie> findPopularSeries(Pageable pageable);

    // Séries com avaliação alta
    Page<Serie> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Por ano
    @Query("SELECT s FROM Serie s WHERE EXTRACT(YEAR FROM s.anoLancamento) = :year")
    Page<Serie> findByYear(@Param("year") Integer year, Pageable pageable);

    // Recomendações baseadas nas categorias que o usuário mais curte
    @Query("SELECT DISTINCT s FROM Serie s JOIN s.categoria cat WHERE cat IN " +
            "(SELECT DISTINCT c FROM Serie serie JOIN serie.categoria c JOIN serie.likes l WHERE l.user.id = :userId) " +
            "AND s.id NOT IN (SELECT l2.serie.id FROM Like l2 WHERE l2.user.id = :userId AND l2.serie IS NOT NULL) " +
            "ORDER BY s.avaliacao DESC")
    Page<Serie> findRecommendations(@Param("userId") UUID userId, Pageable pageable);

    // Séries similares (categorias em comum, excluindo a atual)
    @Query("SELECT DISTINCT s FROM Serie s JOIN s.categoria cat WHERE cat IN :categorias AND s.id != :excludeId")
    Page<Serie> findSimilarSeries(
            @Param("categorias") List<Categoria> categorias,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // Para sitemap
    @Query("SELECT s FROM Serie s WHERE s.title IS NOT NULL AND s.title != ''")
    List<Serie> findAllForSitemap();

    // Busca de texto livre (título e sinopse)
    @Query("SELECT s FROM Serie s WHERE " +
            "UPPER(s.title) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
            "UPPER(s.sinopse) LIKE UPPER(CONCAT('%', :searchTerm, '%'))")
    Page<Serie> searchByTitleOrSinopse(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Contagem de séries por categoria
    @Query("SELECT cat, COUNT(s) FROM Serie s JOIN s.categoria cat GROUP BY cat")
    List<Object[]> countByCategoria();

    // Avaliação média
    @Query("SELECT AVG(m.avaliacao) FROM Movie m WHERE m.avaliacao IS NOT NULL")
    Double getAverageRating();

    // Contagem por ano
    @Query("SELECT EXTRACT(YEAR FROM m.anoLancamento) as year, COUNT(m) FROM Movie m GROUP BY EXTRACT(YEAR FROM m.anoLancamento) ORDER BY year DESC")
    List<Object[]> countByYear();
}