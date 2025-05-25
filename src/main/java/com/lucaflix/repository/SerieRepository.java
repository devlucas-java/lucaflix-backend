package com.lucaflix.repository;

import com.lucaflix.model.Serie;
import com.lucaflix.model.enums.Categoria;
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

    // Buscar séries por categoria ordenadas por data de cadastro
    List<Serie> findByCategoriaOrderByDataCadastroDesc(Categoria categoria, Pageable pageable);

    // Buscar todas as séries ordenadas por data de cadastro
    List<Serie> findAllByOrderByDataCadastroDesc(Pageable pageable);

    // Buscar séries por título (busca parcial, case insensitive)
    List<Serie> findByTitleContainingIgnoreCaseOrderByTitleAsc(String searchTerm);

    // Buscar top 10 séries com mais likes
    @Query("SELECT s FROM Serie s LEFT JOIN s.likes l " +
            "GROUP BY s.id " +
            "ORDER BY COUNT(l) DESC")
    List<Serie> findTop10SeriesByLikes(Pageable pageable);

    // Buscar série por ID com temporadas e episódios carregados
    @Query("SELECT DISTINCT s FROM Serie s " +
            "LEFT JOIN FETCH s.temporadas t " +
            "LEFT JOIN FETCH t.episodios e " +
            "WHERE s.id = :id " +
            "ORDER BY t.numeroTemporada, e.numeroEpisodio")
    Optional<Serie> findByIdWithTemporadasAndEpisodios(@Param("id") Long id);

    // Buscar série por ID com likes carregados (para otimização)
    @Query("SELECT s FROM Serie s LEFT JOIN FETCH s.likes WHERE s.id = :id")
    Optional<Serie> findByIdWithLikes(@Param("id") Long id);

    // Buscar séries mais recentes
    List<Serie> findTop10ByOrderByDataCadastroDesc();

    // Buscar séries por categoria (sem paginação)
    List<Serie> findByCategoriaOrderByDataCadastroDesc(Categoria categoria);

    // Buscar séries por ano de lançamento
    @Query("SELECT s FROM Serie s WHERE YEAR(s.anoLancamento) = :ano ORDER BY s.dataCadastro DESC")
    List<Serie> findByAnoLancamento(@Param("ano") Integer ano);

    // Contar séries por categoria
    long countByCategoria(Categoria categoria);

    // Verificar se existe série com o título
    boolean existsByTitleIgnoreCase(String title);

    // Buscar séries com mais de X temporadas
    @Query("SELECT s FROM Serie s WHERE SIZE(s.temporadas) >= :minTemporadas ORDER BY s.dataCadastro DESC")
    List<Serie> findSeriesWithMinSeasons(@Param("minTemporadas") int minTemporadas);

    // Buscar séries que o usuário está assistindo (tem na lista mas não finalizou)
    @Query("SELECT DISTINCT s FROM Serie s " +
            "JOIN s.minhaLista ml " +
            "WHERE ml.user.id = :userId " +
            "AND ml.episodiosAssistidos < (SELECT COUNT(e) FROM Episodio e JOIN e.temporada t WHERE t.serie = s)")
    List<Serie> findSeriesBeingWatchedByUser(@Param("userId") UUID userId);
}