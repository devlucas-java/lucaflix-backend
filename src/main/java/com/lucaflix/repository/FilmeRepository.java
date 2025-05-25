package com.lucaflix.repository;

import com.lucaflix.model.Filme;
import com.lucaflix.model.enums.Categoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {

    // Buscar filmes por categoria ordenados por data de cadastro
    List<Filme> findByCategoriaOrderByDataCadastroDesc(Categoria categoria, Pageable pageable);

    // Buscar todos os filmes ordenados por data de cadastro
    List<Filme> findAllByOrderByDataCadastroDesc(Pageable pageable);

    // Buscar filmes por título (busca parcial, case insensitive)
    List<Filme> findByTitleContainingIgnoreCaseOrderByTitleAsc(String searchTerm);

    // Buscar top 10 filmes com mais likes
    @Query("SELECT f FROM Filme f LEFT JOIN f.likes l " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l) DESC")
    List<Filme> findTop10FilmesByLikes(Pageable pageable);

    // Buscar filme por ID com likes carregados (para otimização)
    @Query("SELECT f FROM Filme f LEFT JOIN FETCH f.likes WHERE f.id = :id")
    Optional<Filme> findByIdWithLikes(@Param("id") Long id);

    // Buscar filmes mais recentes
    List<Filme> findTop10ByOrderByDataCadastroDesc();

    // Buscar filmes por categoria (sem paginação)
    List<Filme> findByCategoriaOrderByDataCadastroDesc(Categoria categoria);

    // Buscar filmes por ano de lançamento
    @Query("SELECT f FROM Filme f WHERE YEAR(f.anoLancamento) = :ano ORDER BY f.dataCadastro DESC")
    List<Filme> findByAnoLancamento(@Param("ano") Integer ano);

    // Contar filmes por categoria
    long countByCategoria(Categoria categoria);

    // Verificar se existe filme com o título
    boolean existsByTitleIgnoreCase(String title);
}