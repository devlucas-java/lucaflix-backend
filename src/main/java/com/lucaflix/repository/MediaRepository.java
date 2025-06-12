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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query("SELECT m FROM Media m LEFT JOIN m.likes l GROUP BY m.id ORDER BY COUNT(l) DESC")
    List<Media> findTop10ByLikes(Pageable pageable);

    long countByIsFilmeTrue();

    long countByIsFilmeFalse();

    @Query("SELECT AVG(m.avaliacao) FROM Media m")
    Double getAverageRating();

    @Query("SELECT m FROM Media m WHERE " +
            "(:isFilme IS NULL OR m.isFilme = :isFilme) AND " +
            "(:title IS NULL OR UPPER(CAST(m.title AS string)) LIKE UPPER(CONCAT('%', :title, '%'))) AND " +
            "(:avaliacao IS NULL OR m.avaliacao >= :avaliacao) AND " +
            "(:anoInicio IS NULL OR m.anoLancamento >= :anoInicio) AND " +
            "(:anoFim IS NULL OR m.anoLancamento <= :anoFim) AND " +
            "(:categoria IS NULL OR :categoria MEMBER OF m.categoria)")
    Page<Media> buscarPorFiltros(
            @Param("isFilme") Boolean isFilme,
            @Param("title") String title,
            @Param("avaliacao") Double avaliacao,
            @Param("anoInicio") Date anoLancamentoInicio,
            @Param("anoFim") Date anoLancamentoFim,
            @Param("categoria") Categoria categoria,
            Pageable pageable);

    // Queries nativas corrigidas para PostgreSQL
    @Query(value = "SELECT CAST(m.title AS VARCHAR) FROM media m " +
            "LEFT JOIN likes l ON m.id = l.media_id " +
            "GROUP BY m.title " +
            "ORDER BY COUNT(l.id) DESC " +
            "LIMIT 1", nativeQuery = true)
    String findMostLikedMediaTitleNative();

    @Query(value = "SELECT CAST(m.categoria AS VARCHAR) FROM media m " +
            "GROUP BY m.categoria " +
            "ORDER BY COUNT(m.id) DESC " +
            "LIMIT 1", nativeQuery = true)
    String findMostPopularCategoryNative();

    // Query para pegar estatísticas de likes de forma mais eficiente
    @Query(value = "SELECT COUNT(DISTINCT l.media_id) FROM likes l", nativeQuery = true)
    Long countMediasWithLikes();

    // Query para pegar a mídia específica mais curtida (com todos os dados)
    @Query(value = "SELECT m.* FROM media m " +
            "LEFT JOIN likes l ON m.id = l.media_id " +
            "GROUP BY m.id, m.title, m.is_filme, m.ano_lancamento, m.duracao_minutos, m.sinopse, m.data_cadastro, m.categoria, m.min_age, m.avaliacao, m.embed_url_1, m.embed_url_2, m.trailer_url, m.image_url " +
            "ORDER BY COUNT(l.id) DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Media> findMostLikedMediaNative();

    // Mídias com avaliação alta
    Page<Media> findByAvaliacaoGreaterThanEqual(Double avaliacao, Pageable pageable);

    // Filmes e séries separados
    Page<Media> findByIsFilmeTrue(Pageable pageable);
    Page<Media> findByIsFilmeFalse(Pageable pageable);

    // Por categoria - AJUSTADO para lista de categorias
    @Query("SELECT m FROM Media m WHERE :categoria MEMBER OF m.categoria")
    Page<Media> findByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);

    // Por múltiplas categorias
    @Query("SELECT DISTINCT m FROM Media m JOIN m.categoria c WHERE c IN :categorias")
    Page<Media> findByCategoriaIn(@Param("categorias") List<Categoria> categorias, Pageable pageable);

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
    @Query("SELECT m FROM Media m WHERE EXTRACT(YEAR FROM m.anoLancamento) = :year")
    Page<Media> findByYear(@Param("year") Integer year, Pageable pageable);

    // Recomendações baseadas nas categorias que o usuário mais curte - AJUSTADO
    @Query("SELECT DISTINCT m FROM Media m JOIN m.categoria cat WHERE cat IN " +
            "(SELECT DISTINCT c FROM Media media JOIN media.categoria c JOIN media.likes l WHERE l.user.id = :userId) " +
            "AND m.id NOT IN (SELECT l2.media.id FROM Like l2 WHERE l2.user.id = :userId) " +
            "ORDER BY m.avaliacao DESC")
    Page<Media> findRecommendations(@Param("userId") UUID userId, Pageable pageable);

    // Mídias similares (categorias em comum, excluindo a atual) - AJUSTADO
    @Query("SELECT DISTINCT m FROM Media m JOIN m.categoria cat WHERE cat IN :categorias AND m.id != :excludeId")
    Page<Media> findSimilarMedia(
            @Param("categorias") List<Categoria> categorias,
            @Param("excludeId") Long excludeId,
            Pageable pageable);

    // Busca uma única mídia por título e ano exatos - Corrigida
    @Query("SELECT m FROM Media m WHERE UPPER(CAST(m.title AS string)) = UPPER(:title) AND EXTRACT(YEAR FROM m.anoLancamento) = :year")
    Optional<Media> findByTitleAndYear(@Param("title") String title, @Param("year") Integer year);

    @Query("SELECT m FROM Media m WHERE m.title IS NOT NULL AND m.title != ''")
    List<Media> findAllForSitemap();

    // Busca mídias por tipo (filme ou série)
    List<Media> findByIsFilme(boolean isFilme);
}