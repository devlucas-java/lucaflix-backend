package com.lucaflix.repository;

import com.lucaflix.model.MinhaLista;
import com.lucaflix.model.Media;
import com.lucaflix.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MinhaListaRepository extends JpaRepository<MinhaLista, Long> {

    // Verifica se a mídia já está na lista do usuário
    boolean existsByUserAndMedia(User user, Media media);

    // Busca a entrada específica da lista do usuário
    Optional<MinhaLista> findByUserAndMedia(User user, Media media);

    // Busca toda a lista do usuário com paginação
    Page<MinhaLista> findByUser(User user, Pageable pageable);

    void deleteByMedia(Media media);

    @Query("SELECT COUNT(DISTINCT u.id) FROM MinhaLista ml JOIN ml.user u")
    long countDistinctUsers();

}