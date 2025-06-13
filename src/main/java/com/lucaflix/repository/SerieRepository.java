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

    // NOVA QUERY OTIMIZADA PARA BUSCAR SÉRIE COM TEMPORADAS E EPISÓDIOS
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN FETCH s.temporadas t " +
            "LEFT JOIN FETCH t.episodios e " +
            "WHERE s.id = :id " +
            "ORDER BY t.numeroTemporada ASC, e.numeroEpisodio ASC")
    Optional<Serie> findByIdWithTemporadasAndEpisodios(@Param("id") Long id);

    // Top 10 mais curtidas
    @Query("SELECT s FROM Serie s LEFT JOIN s.likes l GROUP BY s.id ORDER BY COUNT(l) DESC")
    List<Serie> findTop10ByLikes(Pageable pageable);

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

    // Contagem de séries por categoria
    @Query("SELECT cat, COUNT(s) FROM Serie s JOIN s.categoria cat GROUP BY cat")
    List<Object[]> countByCategoria();

    // Avaliação média
    @Query("SELECT AVG(s.avaliacao) FROM Serie s WHERE s.avaliacao IS NOT NULL")
    Double getAverageRating();

    // Contagem por ano
    @Query("SELECT EXTRACT(YEAR FROM s.anoLancamento) as year, COUNT(s) FROM Serie s GROUP BY EXTRACT(YEAR FROM s.anoLancamento) ORDER BY year DESC")
    List<Object[]> countByYear();

    // Busca de séries
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN s.categoria c " +
            "WHERE (:texto IS NULL OR " +
            "       LOWER(s.title) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "       LOWER(s.sinopse) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
            "AND (:categoria IS NULL OR c = :categoria) " +
            "ORDER BY s.dataCadastro DESC")
    Page<Serie> searchSeries(@Param("texto") String texto,
                             @Param("categoria") Categoria categoria,
                             Pageable pageable);
}