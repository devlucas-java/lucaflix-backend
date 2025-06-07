package com.lucaflix.repository;

import com.lucaflix.model.Media;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {


    @Query("SELECT m FROM Media m LEFT JOIN m.likes l GROUP BY m.id ORDER BY COUNT(l) DESC")
    List<Media> findTop10ByLikes(Pageable pageable);



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
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:avaliacao IS NULL OR m.avaliacao >= :avaliacao) AND " +
            "(:anoInicio IS NULL OR m.anoLancamento >= :anoInicio) AND " +
            "(:anoFim IS NULL OR m.anoLancamento <= :anoFim) AND " +
            "(:categoria IS NULL OR m.categoria = :categoria)")
    Page<Media> buscarPorFiltros(
            @Param("isFilme") Boolean isFilme,
            @Param("title") String title,
            @Param("avaliacao") Double avaliacao,
            @Param("anoInicio") Date anoLancamentoInicio,
            @Param("anoFim") Date anoLancamentoFim,
            @Param("categoria") Categoria categoria,
            Pageable pageable);

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






    // Adicione estas queries na interface MediaRepository

    // Mídias com avaliação alta
    Page<Media> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Filmes e séries separados
    Page<Media> findByIsFilmeTrue(Pageable pageable);
    Page<Media> findByIsFilmeFalse(Pageable pageable);

    // Por categoria
    Page<Media> findByCategoria(Categoria categoria, Pageable pageable);

    // Por faixa etária
    Page<Media> findByMinAge(String minAge, Pageable pageable);

    // Filmes populares (mais curtidos)
    @Query("SELECT m FROM Media m LEFT JOIN m.likes l WHERE m.isFilme = true GROUP BY m ORDER BY COUNT(l) DESC")
    Page<Media> findPopularMovies(Pageable pageable);

    // Séries populares (mais curtidas)
    @Query("SELECT m FROM Media m LEFT JOIN m.likes l WHERE m.isFilme = false GROUP BY m ORDER BY COUNT(l) DESC")
    Page<Media> findPopularSeries(Pageable pageable);

    // Por duração
    @Query("SELECT m FROM Media m WHERE " +
            "(:minDuration IS NULL OR m.duracaoMinutos >= :minDuration) AND " +
            "(:maxDuration IS NULL OR m.duracaoMinutos <= :maxDuration)")
    Page<Media> findByDurationRange(
            @Param("minDuration") Integer minDuration,
            @Param("maxDuration") Integer maxDuration,
            Pageable pageable);

    // Por ano
    @Query("SELECT m FROM Media m WHERE YEAR(m.anoLancamento) = :year")
    Page<Media> findByYear(@Param("year") Integer year, Pageable pageable);

    // Recomendações baseadas nas categorias que o usuário mais curte
    @Query("SELECT m FROM Media m WHERE m.categoria IN " +
            "(SELECT DISTINCT media.categoria FROM Like l JOIN l.media media WHERE l.user.id = :userId) " +
            "AND m.id NOT IN (SELECT l2.media.id FROM Like l2 WHERE l2.user.id = :userId) " +
            "ORDER BY m.avaliacao DESC")
    Page<Media> findRecommendations(@Param("userId") UUID userId, Pageable pageable);

    // Mídias similares (mesma categoria, excluindo a atual)
    @Query("SELECT m FROM Media m WHERE m.categoria = :categoria AND m.id != :excludeId")
    Page<Media> findSimilarMedia(
            @Param("categoria") Categoria categoria,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // Mídias por múltiplas categorias
    @Query("SELECT m FROM Media m WHERE m.categoria IN :categorias")
    Page<Media> findByCategoriaIn(@Param("categorias") List<Categoria> categorias, Pageable pageable);

    // Mídias lançadas nos últimos X dias
    @Query("SELECT m FROM Media m WHERE m.dataCadastro >= :dataLimite")
    Page<Media> findRecentMedia(@Param("dataLimite") Date dataLimite, Pageable pageable);

    // Mídias mais assistidas (baseado na quantidade de vezes que estão em listas)
    @Query("SELECT m FROM Media m LEFT JOIN m.minhaLista ml GROUP BY m ORDER BY COUNT(ml) DESC")
    Page<Media> findMostWatched(Pageable pageable);

    // Filmes por faixa de duração específica
    @Query("SELECT m FROM Media m WHERE m.isFilme = true AND m.duracaoMinutos BETWEEN :min AND :max")
    Page<Media> findMoviesByDurationRange(
            @Param("min") Integer minDuration,
            @Param("max") Integer maxDuration,
            Pageable pageable);

    // Séries por faixa de duração específica
    @Query("SELECT m FROM Media m WHERE m.isFilme = false AND m.duracaoMinutos BETWEEN :min AND :max")
    Page<Media> findSeriesByDurationRange(
            @Param("min") Integer minDuration,
            @Param("max") Integer maxDuration,
            Pageable pageable);

    // Busca por múltiplos critérios (versão mais flexível)
    @Query("SELECT m FROM Media m WHERE " +
            "(:isFilme IS NULL OR m.isFilme = :isFilme) AND " +
            "(:categoria IS NULL OR m.categoria = :categoria) AND " +
            "(:minAvaliacao IS NULL OR m.avaliacao >= :minAvaliacao) AND " +
            "(:minAge IS NULL OR m.minAge = :minAge) AND " +
            "(:minDuration IS NULL OR m.duracaoMinutos >= :minDuration) AND " +
            "(:maxDuration IS NULL OR m.duracaoMinutos <= :maxDuration)")
    Page<Media> findByMultipleCriteria(
            @Param("isFilme") Boolean isFilme,
            @Param("categoria") Categoria categoria,
            @Param("minAvaliacao") Double minAvaliacao,
            @Param("minAge") String minAge,
            @Param("minDuration") Integer minDuration,
            @Param("maxDuration") Integer maxDuration,
            Pageable pageable);






}