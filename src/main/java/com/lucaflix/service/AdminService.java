package com.lucaflix.service;

import com.lucaflix.model.*;
import com.lucaflix.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private TemporadaRepository temporadaRepository;

    @Autowired
    private EpisodioRepository episodioRepository;

    @Autowired
    private UserRepository userRepository;

    // SERVIÇOS DE FILMES

    /**
     * Adiciona um novo filme ao catálogo
     * @param filme dados do filme a ser adicionado
     * @return filme salvo no banco de dados
     */
    public Filme adicionarFilme(Filme filme) {
        return filmeRepository.save(filme);
    }

    /**
     * Atualiza um filme existente
     * @param id ID do filme a ser atualizado
     * @param filmeAtualizado dados atualizados do filme
     * @return filme atualizado ou null se não encontrado
     */
    public Filme atualizarFilme(Long id, Filme filmeAtualizado) {
        Optional<Filme> filmeExistente = filmeRepository.findById(id);

        if (filmeExistente.isPresent()) {
            filmeAtualizado.setId(id);
            return filmeRepository.save(filmeAtualizado);
        }

        return null;
    }

    /**
     * Exclui um filme do catálogo
     * @param id ID do filme a ser excluído
     * @return true se o filme foi excluído, false se não foi encontrado
     */
    public boolean excluirFilme(Long id) {
        Optional<Filme> filme = filmeRepository.findById(id);

        if (filme.isPresent()) {
            filmeRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     * Lista todos os filmes cadastrados
     * @return lista de todos os filmes
     */
    public List<Filme> listarFilmes() {
        return filmeRepository.findAll();
    }

    // SERVIÇOS DE SÉRIES

    /**
     * Adiciona uma nova série ao catálogo
     * @param serie dados da série a ser adicionada
     * @return série salva no banco de dados
     */
    public Serie adicionarSerie(Serie serie) {
        return serieRepository.save(serie);
    }

    /**
     * Atualiza uma série existente
     * @param id ID da série a ser atualizada
     * @param serieAtualizada dados atualizados da série
     * @return série atualizada ou null se não encontrada
     */
    public Serie atualizarSerie(Long id, Serie serieAtualizada) {
        Optional<Serie> serieExistente = serieRepository.findById(id);

        if (serieExistente.isPresent()) {
            serieAtualizada.setId(id);
            return serieRepository.save(serieAtualizada);
        }

        return null;
    }

    /**
     * Exclui uma série do catálogo
     * @param id ID da série a ser excluída
     * @return true se a série foi excluída, false se não foi encontrada
     */
    public boolean excluirSerie(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()) {
            serieRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     * Lista todas as séries cadastradas
     * @return lista de todas as séries
     */
    public List<Serie> listarSeries() {
        return serieRepository.findAll();
    }

    // SERVIÇOS DE TEMPORADAS

    /**
     * Adiciona uma nova temporada a uma série
     * @param serieId ID da série
     * @param temporada dados da temporada a ser adicionada
     * @return temporada salva ou null se a série não for encontrada
     */
    public Temporada adicionarTemporada(Long serieId, Temporada temporada) {
        Optional<Serie> serie = serieRepository.findById(serieId);

        if (serie.isPresent()) {
            temporada.setSerie(serie.get());
            return temporadaRepository.save(temporada);
        }

        return null;
    }

    /**
     * Atualiza uma temporada existente
     * @param id ID da temporada a ser atualizada
     * @param temporadaAtualizada dados atualizados da temporada
     * @return temporada atualizada ou null se não encontrada
     */
    public Temporada atualizarTemporada(Long id, Temporada temporadaAtualizada) {
        Optional<Temporada> temporadaExistente = temporadaRepository.findById(id);

        if (temporadaExistente.isPresent()) {
            temporadaAtualizada.setSerie(temporadaExistente.get().getSerie());
            temporadaAtualizada.setId(id);
            return temporadaRepository.save(temporadaAtualizada);
        }

        return null;
    }

    /**
     * Exclui uma temporada e todos seus episódios
     * @param id ID da temporada a ser excluída
     * @return true se a temporada foi excluída, false se não foi encontrada
     */
    public boolean excluirTemporada(Long id) {
        Optional<Temporada> temporada = temporadaRepository.findById(id);

        if (temporada.isPresent()) {
            temporadaRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     * Lista todas as temporadas de uma série específica
     * @param serieId ID da série
     * @return lista de temporadas ou null se a série não for encontrada
     */
    public List<Temporada> listarTemporadasDaSerie(Long serieId) {
        Optional<Serie> serie = serieRepository.findById(serieId);

        if (serie.isPresent()) {
            return serie.get().getTemporadas();
        }

        return null;
    }

    // SERVIÇOS DE EPISÓDIOS

    /**
     * Adiciona um novo episódio a uma temporada
     * @param temporadaId ID da temporada
     * @param episodio dados do episódio a ser adicionado
     * @return episódio salvo ou null se a temporada não for encontrada
     */
    public Episodio adicionarEpisodio(Long temporadaId, Episodio episodio) {
        Optional<Temporada> temporada = temporadaRepository.findById(temporadaId);

        if (temporada.isPresent()) {
            episodio.setTemporada(temporada.get());
            return episodioRepository.save(episodio);
        }

        return null;
    }

    /**
     * Atualiza um episódio existente
     * @param id ID do episódio a ser atualizado
     * @param episodioAtualizado dados atualizados do episódio
     * @return episódio atualizado ou null se não encontrado
     */
    public Episodio atualizarEpisodio(Long id, Episodio episodioAtualizado) {
        Optional<Episodio> episodioExistente = episodioRepository.findById(id);

        if (episodioExistente.isPresent()) {
            episodioAtualizado.setTemporada(episodioExistente.get().getTemporada());
            episodioAtualizado.setId(id);
            return episodioRepository.save(episodioAtualizado);
        }

        return null;
    }

    /**
     * Exclui um episódio específico
     * @param id ID do episódio a ser excluído
     * @return true se o episódio foi excluído, false se não foi encontrado
     */
    public boolean excluirEpisodio(Long id) {
        Optional<Episodio> episodio = episodioRepository.findById(id);

        if (episodio.isPresent()) {
            episodioRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     * Lista todos os episódios de uma temporada específica
     * @param temporadaId ID da temporada
     * @return lista de episódios ou null se a temporada não for encontrada
     */
    public List<Episodio> listarEpisodiosDaTemporada(Long temporadaId) {
        Optional<Temporada> temporada = temporadaRepository.findById(temporadaId);

        if (temporada.isPresent()) {
            return temporada.get().getEpisodios();
        }

        return null;
    }

    // MÉTODOS AUXILIARES

    /**
     * Verifica se o usuário tem permissões de administrador
     * @param user usuário a ser verificado
     * @return true se o usuário é admin ou super admin, false caso contrário
     */
    public boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }

        return user.getRole().name().equals("ADMIN") || user.getRole().name().equals("SUPER_ADMIN");
    }
}