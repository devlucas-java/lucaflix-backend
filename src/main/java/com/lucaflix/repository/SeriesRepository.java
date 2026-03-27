package com.lucaflix.repository;

import com.lucaflix.model.Series;
import com.lucaflix.model.Season;
import com.lucaflix.model.enums.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeriesRepository extends JpaRepository<Series, UUID>, JpaSpecificationExecutor<Series> {

    // Fetch série com temporadas para evitar MultipleBagFetchException
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN FETCH s.temporadas " +
            "WHERE s.id = :id")
    Optional<Series> findByIdWithTemporadas(@Param("id") Long id);

    // Query separada para buscar temporadas com episódios
    @Query("SELECT DISTINCT t FROM Temporada t " +
            "LEFT JOIN FETCH t.episodios " +
            "WHERE t.serie.id = :serieId " +
            "ORDER BY t.numeroTemporada ASC")
    List<Season> findTemporadasWithEpisodiosBySerieId(@Param("serieId") Long serieId);

    // Top 10 mais curtidas
    @Query("SELECT s FROM Serie s " +
            "LEFT JOIN s.likes l " +
            "GROUP BY s.id, s.title, s.anoLancamento, s.tmdbId, s.imdbId, s.paisOrigem, " +
            "s.sinopse, s.dataCadastro, s.minAge, s.avaliacao, s.trailer, " +
            "s.posterURL1, s.posterURL2, s.backdropURL1, s.backdropURL2, s.backdropURL3, s.backdropURL4, " +
            "s.logoURL1, s.logoURL2, s.totalTemporadas, s.totalEpisodios " +
            "ORDER BY COUNT(l) DESC")
    List<Series> findTop10ByLikes(Pageable pageable);

    // Por categoria
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.categoria c " +
            "WHERE c = :categoria " +
            "ORDER BY s.dataCadastro DESC")
    Page<Series> findByCategoria(@Param("categoria") Categories categories, Pageable pageable);

    // Séries populares (mais curtidas)
    @Query("SELECT s FROM Serie s " +
            "LEFT JOIN s.likes l " +
            "GROUP BY s.id, s.title, s.anoLancamento, s.tmdbId, s.imdbId, s.paisOrigem, " +
            "s.sinopse, s.dataCadastro, s.minAge, s.avaliacao, s.trailer, " +
            "s.posterURL1, s.posterURL2, s.backdropURL1, s.backdropURL2, s.backdropURL3, s.backdropURL4, " +
            "s.logoURL1, s.logoURL2, s.totalTemporadas, s.totalEpisodios " +
            "ORDER BY COUNT(l) DESC")
    Page<Series> findPopularSeries(Pageable pageable);

    // Séries com avaliação alta
    Page<Series> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Por ano - CORRIGIDO para usar Integer
    @Query("SELECT s FROM Serie s " +
            "WHERE s.anoLancamento = :year " +
            "ORDER BY s.avaliacao DESC")
    Page<Series> findByYear(@Param("year") Integer year, Pageable pageable);

    // Recomendações baseadas nas categorias - Corrigida para tratar userId null
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.categoria cat " +
            "WHERE (:userId IS NULL OR cat IN (" +
            "    SELECT DISTINCT c FROM Serie serie " +
            "    JOIN serie.categoria c " +
            "    JOIN serie.likes l " +
            "    WHERE l.user.id = :userId" +
            ")) " +
            "AND (:userId IS NULL OR s.id NOT IN (" +
            "    SELECT l2.serie.id FROM Like l2 " +
            "    WHERE l2.user.id = :userId AND l2.serie IS NOT NULL" +
            ")) " +
            "ORDER BY s.avaliacao DESC")
    Page<Series> findRecommendations(@Param("userId") UUID userId, Pageable pageable);

    // Séries similares (categorias em comum, excluindo a atual)
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.categoria cat " +
            "WHERE cat IN :categorias " +
            "AND s.id != :excludeId " +
            "ORDER BY s.avaliacao DESC")
    Page<Series> findSimilarSeries(
            @Param("categorias") List<Categories> categories,
            @Param("excludeId") UUID excludeId,
            Pageable pageable);

    // Para sitemap
    @Query("SELECT s FROM Serie s WHERE s.title IS NOT NULL AND s.title != ''")
    List<Series> findAllForSitemap();

    // Busca de séries
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN s.categoria c " +
            "WHERE (:texto IS NULL OR :texto = '' OR " +
            "       LOWER(s.title) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "       LOWER(s.sinopse) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
            "AND (:categoria IS NULL OR c = :categoria) " +
            "ORDER BY s.dataCadastro DESC")
    Page<Series> searchSeries(@Param("texto") String texto,
                              @Param("categoria") Categories categories,
                              Pageable pageable);

    // Verificar se existem séries
    @Query("SELECT COUNT(s) FROM Serie s")
    long countAllSeries();
}