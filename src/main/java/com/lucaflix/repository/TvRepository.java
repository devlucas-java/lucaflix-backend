package com.lucaflix.repository;

import com.lucaflix.model.Tv;
import com.lucaflix.model.enums.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TvRepository extends JpaRepository<Tv, Long> {

    // Buscar por título (case insensitive)
    List<Tv> findByTitleContainingIgnoreCase(String title);

    // Buscar por título com paginação
    Page<Tv> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Buscar por categoria
    List<Tv> findByCategoria(Categoria categoria);

    // Buscar por categoria com paginação
    Page<Tv> findByCategoria(Categoria categoria, Pageable pageable);

    // Buscar por país de origem
    List<Tv> findByPaisOrigenIgnoreCase(String paisOrigen);

    // Buscar por país de origem com paginação
    Page<Tv> findByPaisOrigenIgnoreCase(String paisOrigen, Pageable pageable);

    // Buscar por idade mínima
    List<Tv> findByMinAge(String minAge);

    // Buscar TVs com mais de X likes
    List<Tv> findByLikesGreaterThan(Long likes);

    // Buscar TVs com mais de X likes com paginação
    Page<Tv> findByLikesGreaterThan(Long likes, Pageable pageable);

    // Buscar as TVs mais populares (ordenadas por likes)
    @Query("SELECT t FROM Tv t ORDER BY t.likes DESC")
    List<Tv> findMostPopular(Pageable pageable);

    // Buscar por múltiplos critérios
    @Query("SELECT t FROM Tv t WHERE " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:categoria IS NULL OR t.categoria = :categoria) AND " +
            "(:paisOrigen IS NULL OR LOWER(t.paisOrigen) = LOWER(:paisOrigen)) AND " +
            "(:minAge IS NULL OR t.minAge = :minAge)")
    Page<Tv> findByMultipleCriteria(
            @Param("title") String title,
            @Param("categoria") Categoria categoria,
            @Param("paisOrigen") String paisOrigen,
            @Param("minAge") String minAge,
            Pageable pageable);

    // Buscar TVs recentes (últimos registros)
    @Query("SELECT t FROM Tv t ORDER BY t.dataCadastro DESC")
    List<Tv> findRecentTvs(Pageable pageable);

    // Contar TVs por categoria
    @Query("SELECT COUNT(t) FROM Tv t WHERE t.categoria = :categoria")
    Long countByCategoria(@Param("categoria") Categoria categoria);

    // Buscar TVs por categoria e país
    List<Tv> findByCategoriaAndPaisOrigenIgnoreCase(Categoria categoria, String paisOrigen);

    // Buscar TVs que tenham pelo menos uma URL de embed
    @Query("SELECT t FROM Tv t WHERE t.embed1 IS NOT NULL OR t.embed2 IS NOT NULL")
    List<Tv> findTvsWithEmbedUrls();

    // Buscar TVs que tenham pelo menos uma imagem
    @Query("SELECT t FROM Tv t WHERE t.imageURL1 IS NOT NULL OR t.imageURL2 IS NOT NULL")
    List<Tv> findTvsWithImages();

    // Verificar se existe TV com o mesmo título
    boolean existsByTitleIgnoreCase(String title);

    // Buscar TV por título exato (case insensitive)
    Optional<Tv> findByTitleIgnoreCase(String title);

    // Buscar todas as categorias disponíveis
    @Query("SELECT DISTINCT t.categoria FROM Tv t ORDER BY t.categoria")
    List<Categoria> findAllDistinctCategorias();

    // Buscar todos os países disponíveis
    @Query("SELECT DISTINCT t.paisOrigen FROM Tv t WHERE t.paisOrigen IS NOT NULL ORDER BY t.paisOrigen")
    List<String> findAllDistinctPaises();

    // Estatísticas: TV com mais likes
    @Query("SELECT t FROM Tv t WHERE t.likes = (SELECT MAX(tv.likes) FROM Tv tv)")
    List<Tv> findTvWithMaxLikes();

    // Buscar TVs por faixa de likes
    @Query("SELECT t FROM Tv t WHERE t.likes BETWEEN :minLikes AND :maxLikes ORDER BY t.likes DESC")
    Page<Tv> findByLikesBetween(
            @Param("minLikes") Long minLikes,
            @Param("maxLikes") Long maxLikes,
            Pageable pageable);
}