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

    // QUERY CORRIGIDA - Fazendo fetch em etapas para evitar MultipleBagFetchException
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN FETCH s.temporadas " +
            "WHERE s.id = :id")
    Optional<Serie> findByIdWithTemporadas(@Param("id") Long id);

    // Query separada para buscar temporadas com episódios
    @Query("SELECT DISTINCT t FROM Temporada t " +
            "LEFT JOIN FETCH t.episodios " +
            "WHERE t.serie.id = :serieId " +
            "ORDER BY t.numeroTemporada ASC")
    List<com.lucaflix.model.Temporada> findTemporadasWithEpisodiosBySerieId(@Param("serieId") Long serieId);

    // Top 10 mais curtidas - CORRIGIDA
    @Query("SELECT s FROM Serie s " +
            "LEFT JOIN s.likes l " +
            "GROUP BY s.id, s.title, s.anoLancamento, s.tmdbId, s.imdbId, s.paisOrigem, " +
            "s.sinopse, s.dataCadastro, s.minAge, s.avaliacao, s.trailer, s.imageURL1, " +
            "s.imageURL2, s.totalTemporadas, s.totalEpisodios " +
            "ORDER BY COUNT(l) DESC")
    List<Serie> findTop10ByLikes(Pageable pageable);

    // Por categoria - CORRIGIDA
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.categoria c " +
            "WHERE c = :categoria " +
            "ORDER BY s.dataCadastro DESC")
    Page<Serie> findByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);

    // Séries populares (mais curtidas) - CORRIGIDA
    @Query("SELECT s FROM Serie s " +
            "LEFT JOIN s.likes l " +
            "GROUP BY s.id, s.title, s.anoLancamento, s.tmdbId, s.imdbId, s.paisOrigem, " +
            "s.sinopse, s.dataCadastro, s.minAge, s.avaliacao, s.trailer, s.imageURL1, " +
            "s.imageURL2, s.totalTemporadas, s.totalEpisodios " +
            "ORDER BY COUNT(l) DESC")
    Page<Serie> findPopularSeries(Pageable pageable);

    // Séries com avaliação alta - MANTIDA (estava correta)
    Page<Serie> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Por ano - CORRIGIDA
    @Query("SELECT s FROM Serie s " +
            "WHERE YEAR(s.anoLancamento) = :year " +
            "ORDER BY s.avaliacao DESC")
    Page<Serie> findByYear(@Param("year") Integer year, Pageable pageable);

    // Recomendações baseadas nas categorias - CORRIGIDA para tratar userId null
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

    // Séries similares (categorias em comum, excluindo a atual) - CORRIGIDA
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.categoria cat " +
            "WHERE cat IN :categorias " +
            "AND s.id != :excludeId " +
            "ORDER BY s.avaliacao DESC")
    Page<Serie> findSimilarSeries(
            @Param("categorias") List<Categoria> categorias,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // Para sitemap - MANTIDA
    @Query("SELECT s FROM Serie s WHERE s.title IS NOT NULL AND s.title != ''")
    List<Serie> findAllForSitemap();

    // Contagem de séries por categoria - CORRIGIDA
    @Query("SELECT cat, COUNT(s) FROM Serie s " +
            "JOIN s.categoria cat " +
            "GROUP BY cat")
    List<Object[]> countByCategoria();

    // Avaliação média - MANTIDA
    @Query("SELECT AVG(s.avaliacao) FROM Serie s WHERE s.avaliacao IS NOT NULL")
    Double getAverageRating();

    // Contagem por ano - CORRIGIDA
    @Query("SELECT YEAR(s.anoLancamento) as year, COUNT(s) " +
            "FROM Serie s " +
            "WHERE s.anoLancamento IS NOT NULL " +
            "GROUP BY YEAR(s.anoLancamento) " +
            "ORDER BY year DESC")
    List<Object[]> countByYear();

    // Busca de séries - CORRIGIDA
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

    // Verificar se usuário curtiu uma série - NOVO MÉTODO
    @Query("SELECT COUNT(l) > 0 FROM Like l " +
            "WHERE (:userId IS NULL OR l.user.id = :userId) " +
            "AND l.serie.id = :serieId")
    boolean existsLikeByUserAndSerie(@Param("userId") UUID userId, @Param("serieId") Long serieId);

    // Verificar se série está na lista do usuário - NOVO MÉTODO
    @Query("SELECT COUNT(ml) > 0 FROM MinhaLista ml " +
            "WHERE (:userId IS NULL OR ml.user.id = :userId) " +
            "AND ml.serie.id = :serieId")
    boolean existsInMyListByUserAndSerie(@Param("userId") UUID userId, @Param("serieId") Long serieId);

    // MÉTODOS ADICIONAIS PARA DEBUGGING E FUNCIONALIDADES EXTRAS

    // Verificar se existem séries
    @Query("SELECT COUNT(s) FROM Serie s")
    long countAllSeries();

    // Buscar todas as séries (para debugging)
    @Query("SELECT s FROM Serie s ORDER BY s.dataCadastro DESC")
    Page<Serie> findAllSeries(Pageable pageable);

    // Buscar séries por título (busca simples)
    @Query("SELECT s FROM Serie s " +
            "WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "ORDER BY s.title ASC")
    Page<Serie> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    // Buscar séries sem filtros para testar se há dados
    @Query("SELECT s FROM Serie s")
    List<Serie> findAllSeriesSimple();
}