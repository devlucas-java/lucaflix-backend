package com.lucaflix.repository;

import com.lucaflix.model.Media;
import com.lucaflix.model.enums.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // Top 10 mais curtidas
    @Query("SELECT m FROM Media m LEFT JOIN m.likes l GROUP BY m ORDER BY COUNT(l) DESC")
    List<Media> findTop10ByOrderByLikesDesc();

    long countByIsFilmeTrue();

    long countByIsFilmeFalse();

    @Query("SELECT AVG(m.avaliacao) FROM Media m")
    Double getAverageRating();

    @Query("SELECT m.title FROM Media m LEFT JOIN m.likes l GROUP BY m.title ORDER BY COUNT(l) DESC")
    String findMostLikedMediaTitle();

    @Query("SELECT m.categoria FROM Media m GROUP BY m.categoria ORDER BY COUNT(m) DESC")
    String findMostPopularCategory();

    @Query("SELECT m FROM Media m WHERE " +
            "(:isFilme IS NULL OR m.isFilme = :isFilme) AND " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(:title)) AND " +
            "(:avaliacao IS NULL OR m.avaliacao >= :avaliacao) AND " +
            "(:anoInicio IS NULL OR m.anoLancamento >= :anoInicio) AND " +
            "(:anoFim IS NULL OR m.anoLancamento <= :anoFim) AND " +
            "(:categoria IS NULL OR m.categoria = :categoria)")
    List<Media> buscarPorFiltros(
            @Param("isFilme") Boolean isFilme,
            @Param("title") String title,
            @Param("avaliacao") Double avaliacao,
            @Param("anoInicio") Date anoLancamentoInicio,
            @Param("anoFim") Date anoLancamentoFim,
            @Param("categoria") Categoria categoria);

    @Query("SELECT COUNT(m) FROM Media m WHERE " +
            "(:isFilme IS NULL OR m.isFilme = :isFilme) AND " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(:title)) AND " +
            "(:avaliacao IS NULL OR m.avaliacao >= :avaliacao) AND " +
            "(:anoInicio IS NULL OR m.anoLancamento >= :anoInicio) AND " +
            "(:anoFim IS NULL OR m.anoLancamento <= :anoFim) AND " +
            "(:categoria IS NULL OR m.categoria = :categoria)")
    long contarPorFiltros(
            @Param("isFilme") Boolean isFilme,
            @Param("title") String title,
            @Param("avaliacao") Double avaliacao,
            @Param("anoInicio") Date anoLancamentoInicio,
            @Param("anoFim") Date anoLancamentoFim,
            @Param("categoria") Categoria categoria);

}