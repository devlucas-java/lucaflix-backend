package com.lucaflix.repository;

import com.lucaflix.model.Movie;
import com.lucaflix.model.enums.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID>, JpaSpecificationExecutor<Movie> {

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

    // Filmes similares (categorias em comum, excluindo o atual)
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.categoria cat WHERE cat IN :categorias AND m.id != :excludeId")
    Page<Movie> findSimilarMovies(
            @Param("categorias") List<Categories> categories,
            @Param("excludeId") UUID excludeId,
            Pageable pageable);

    // Para sitemap
    @Query("SELECT m FROM Movie m WHERE m.title IS NOT NULL AND m.title != ''")
    List<Movie> findAllForSitemap();
    
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