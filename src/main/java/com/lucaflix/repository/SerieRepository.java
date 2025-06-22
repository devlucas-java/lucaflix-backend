package com.lucaflix.repository;

import com.lucaflix.model.Serie;
import com.lucaflix.model.Temporada;
import com.lucaflix.model.enums.Categoria;
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
public interface SerieRepository extends JpaRepository<Serie, Long> {

    // Fetch série com temporadas para evitar MultipleBagFetchException
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN FETCH s.temporadas " +
            "WHERE s.id = :id")
    Optional<Serie> findByIdWithTemporadas(@Param("id") Long id);

    // Query separada para buscar temporadas com episódios
    @Query("SELECT DISTINCT t FROM Temporada t " +
            "LEFT JOIN FETCH t.episodios " +
            "WHERE t.serie.id = :serieId " +
            "ORDER BY t.numeroTemporada ASC")
    List<Temporada> findTemporadasWithEpisodiosBySerieId(@Param("serieId") Long serieId);

    // Top 10 mais curtidas
    @Query("SELECT s FROM Serie s " +
            "LEFT JOIN s.likes l " +
            "GROUP BY s.id, s.title, s.anoLancamento, s.tmdbId, s.imdbId, s.paisOrigem, " +
            "s.sinopse, s.dataCadastro, s.minAge, s.avaliacao, s.trailer, " +
            "s.posterURL1, s.posterURL2, s.backdropURL1, s.backdropURL2, s.backdropURL3, s.backdropURL4, " +
            "s.logoURL1, s.logoURL2, s.totalTemporadas, s.totalEpisodios " +
            "ORDER BY COUNT(l) DESC")
    List<Serie> findTop10ByLikes(Pageable pageable);

    // Por categoria
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.categoria c " +
            "WHERE c = :categoria " +
            "ORDER BY s.dataCadastro DESC")
    Page<Serie> findByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);

    // Séries populares (mais curtidas)
    @Query("SELECT s FROM Serie s " +
            "LEFT JOIN s.likes l " +
            "GROUP BY s.id, s.title, s.anoLancamento, s.tmdbId, s.imdbId, s.paisOrigem, " +
            "s.sinopse, s.dataCadastro, s.minAge, s.avaliacao, s.trailer, " +
            "s.posterURL1, s.posterURL2, s.backdropURL1, s.backdropURL2, s.backdropURL3, s.backdropURL4, " +
            "s.logoURL1, s.logoURL2, s.totalTemporadas, s.totalEpisodios " +
            "ORDER BY COUNT(l) DESC")
    Page<Serie> findPopularSeries(Pageable pageable);

    // Séries com avaliação alta
    Page<Serie> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Por ano - CORRIGIDO para usar Integer
    @Query("SELECT s FROM Serie s " +
            "WHERE s.anoLancamento = :year " +
            "ORDER BY s.avaliacao DESC")
    Page<Serie> findByYear(@Param("year") Integer year, Pageable pageable);

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
    Page<Serie> findRecommendations(@Param("userId") UUID userId, Pageable pageable);

    // Séries similares (categorias em comum, excluindo a atual)
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.categoria cat " +
            "WHERE cat IN :categorias " +
            "AND s.id != :excludeId " +
            "ORDER BY s.avaliacao DESC")
    Page<Serie> findSimilarSeries(
            @Param("categorias") List<Categoria> categorias,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // Para sitemap
    @Query("SELECT s FROM Serie s WHERE s.title IS NOT NULL AND s.title != ''")
    List<Serie> findAllForSitemap();

    // Contagem de séries por categoria
    @Query("SELECT cat, COUNT(s) FROM Serie s " +
            "JOIN s.categoria cat " +
            "GROUP BY cat")
    List<Object[]> countByCategoria();

    // Avaliação média
    @Query("SELECT AVG(s.avaliacao) FROM Serie s WHERE s.avaliacao IS NOT NULL")
    Double getAverageRating();

    // Contagem por ano - CORRIGIDO para usar Integer
    @Query("SELECT s.anoLancamento as year, COUNT(s) " +
            "FROM Serie s " +
            "WHERE s.anoLancamento IS NOT NULL " +
            "GROUP BY s.anoLancamento " +
            "ORDER BY year DESC")
    List<Object[]> countByYear();

    // Busca de séries
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN s.categoria c " +
            "WHERE (:texto IS NULL OR :texto = '' OR " +
            "       LOWER(s.title) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "       LOWER(s.sinopse) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
            "AND (:categoria IS NULL OR c = :categoria) " +
            "ORDER BY s.dataCadastro DESC")
    Page<Serie> searchSeries(@Param("texto") String texto,
                             @Param("categoria") Categoria categoria,
                             Pageable pageable);

    // Verificar se usuário curtiu uma série
    @Query("SELECT COUNT(l) > 0 FROM Like l " +
            "WHERE (:userId IS NULL OR l.user.id = :userId) " +
            "AND l.serie.id = :serieId")
    boolean existsLikeByUserAndSerie(@Param("userId") UUID userId, @Param("serieId") Long serieId);

    // Verificar se série está na lista do usuário
    @Query("SELECT COUNT(ml) > 0 FROM MinhaLista ml " +
            "WHERE (:userId IS NULL OR ml.user.id = :userId) " +
            "AND ml.serie.id = :serieId")
    boolean existsInMyListByUserAndSerie(@Param("userId") UUID userId, @Param("serieId") Long serieId);

    // Verificar se existem séries
    @Query("SELECT COUNT(s) FROM Serie s")
    long countAllSeries();

    long countByAvaliacaoGreaterThanEqual(double v);

    long countByAvaliacaoBetween(double v, double v1);

    long countByAvaliacaoLessThan(double v);

    Long countByDataCadastroAfter(Date weekAgoDate);
}