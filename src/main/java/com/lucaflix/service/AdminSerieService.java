package com.lucaflix.service;

import com.lucaflix.dto.admin.*;
import com.lucaflix.dto.media.serie.SerieCompleteDTO;
import com.lucaflix.dto.media.serie.SerieMapper;
import com.lucaflix.model.*;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSerieService {

    private final SerieRepository serieRepository;
    private final TemporadaRepository temporadaRepository;
    private final EpisodioRepository episodioRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final SerieMapper serieMapper; // Injeção do mapper

    // ==================== GERENCIAMENTO DE SÉRIES ====================

    @Transactional
    public SerieCompleteDTO createSerie(CreateSerieDTO createDTO) {
        Serie serie = new Serie();
        serie.setTitle(createDTO.getTitle());
        serie.setAnoLancamento(createDTO.getAnoLancamento());
        serie.setTmdbId(createDTO.getTmdbId());
        serie.setImdbId(createDTO.getImdbId());
        serie.setPaisOrigem(createDTO.getPaisOrigen());
        serie.setSinopse(createDTO.getSinopse());
        serie.setCategoria(createDTO.getCategoria());
        serie.setMinAge(createDTO.getMinAge());
        serie.setAvaliacao(createDTO.getAvaliacao());
        serie.setTrailer(createDTO.getTrailer());
        serie.setPosterURL1(createDTO.getPosterURL1());
        serie.setPosterURL2(createDTO.getPosterURL2());
        serie.setLogoURL1(createDTO.getLogoURL1());
        serie.setLogoURL2(createDTO.getLogoURL2());
        serie.setBackdropURL1(createDTO.getBackdropURL1());
        serie.setBackdropURL2(createDTO.getBackdropURL2());
        serie.setBackdropURL3(createDTO.getBackdropURL3());
        serie.setBackdropURL4(createDTO.getBackdropURL4());
        serie.setDataCadastro(new Date());

        Serie savedSerie = serieRepository.save(serie);
        return serieMapper.convertToCompleteDTO(savedSerie, null); // Admin não precisa de userId
    }

    @Transactional
    public SerieCompleteDTO updateSerie(Long serieId, UpdateSerieDTO updateDTO) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));

        if (updateDTO.getTitle() != null) {
            serie.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getAnoLancamento() != null) {
            serie.setAnoLancamento(updateDTO.getAnoLancamento());
        }
        if (updateDTO.getTmdbId() != null) {
            serie.setTmdbId(updateDTO.getTmdbId());
        }
        if (updateDTO.getImdbId() != null) {
            serie.setImdbId(updateDTO.getImdbId());
        }
        if (updateDTO.getPaisOrigen() != null) {
            serie.setPaisOrigem(updateDTO.getPaisOrigen());
        }
        if (updateDTO.getSinopse() != null) {
            serie.setSinopse(updateDTO.getSinopse());
        }
        if (updateDTO.getCategoria() != null) {
            serie.setCategoria(updateDTO.getCategoria());
        }
        if (updateDTO.getMinAge() != null) {
            serie.setMinAge(updateDTO.getMinAge());
        }
        if (updateDTO.getAvaliacao() != null) {
            serie.setAvaliacao(updateDTO.getAvaliacao());
        }
        if (updateDTO.getTrailer() != null) {
            serie.setTrailer(updateDTO.getTrailer());
        }
        if (updateDTO.getLogoURL1() != null) {
            serie.setLogoURL1(updateDTO.getLogoURL1());
        }
        if (updateDTO.getLogoURL2() != null) {
            serie.setLogoURL1(updateDTO.getLogoURL2());
        }
        if (updateDTO.getBackdropURL1() != null) {
            serie.setBackdropURL1(updateDTO.getBackdropURL1());
        }
        if (updateDTO.getBackdropURL2() != null) {
            serie.setBackdropURL2(updateDTO.getBackdropURL2());
        }
        if (updateDTO.getBackdropURL3() != null) {
            serie.setBackdropURL3(updateDTO.getBackdropURL3());
        }
        if (updateDTO.getBackdropURL4() != null) {
            serie.setBackdropURL4(updateDTO.getBackdropURL4());
        }
        if (updateDTO.getPosterURL1() != null) {
            serie.setPosterURL1(updateDTO.getPosterURL1());
        }
        if (updateDTO.getPosterURL2() != null) {
            serie.setPosterURL2(updateDTO.getPosterURL2());
        }

        Serie updatedSerie = serieRepository.save(serie);
        return serieMapper.convertToCompleteDTO(updatedSerie, null); // Admin não precisa de userId
    }

    @Transactional
    public void deleteSerie(Long serieId) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));

        // Deletar likes e listas primeiro
        likeRepository.deleteBySerie(serie);
        minhaListaRepository.deleteBySerie(serie);

        // Deletar temporadas e episódios (cascade fará isso automaticamente)
        serieRepository.delete(serie);
    }

    public SerieCompleteDTO getSerieById(Long serieId) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));
        return serieMapper.convertToCompleteDTO(serie, null); // Admin não precisa de userId
    }

    // ==================== GERENCIAMENTO DE TEMPORADAS ====================

    @Transactional
    public Temporada createTemporada(Long serieId, CreateTemporadaDTO createDTO) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));

        // Verificar se já existe temporada com esse número
        if (temporadaRepository.existsBySerieAndNumeroTemporada(serie, createDTO.getNumeroTemporada())) {
            throw new RuntimeException("Já existe uma temporada com o número " + createDTO.getNumeroTemporada());
        }

        Temporada temporada = new Temporada();
        temporada.setSerie(serie);
        temporada.setNumeroTemporada(createDTO.getNumeroTemporada());
        temporada.setAnoLancamento(createDTO.getAnoLancamento());
        temporada.setDataCadastro(new Date());

        Temporada savedTemporada = temporadaRepository.save(temporada);
        updateSerieStats(serie);

        return savedTemporada;
    }

    @Transactional
    public Temporada updateTemporada(Long temporadaId, UpdateTemporadaDTO updateDTO) {
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada"));

        if (updateDTO.getNumeroTemporada() != null) {
            // Verificar se novo número não conflita com existente
            if (!temporada.getNumeroTemporada().equals(updateDTO.getNumeroTemporada()) &&
                    temporadaRepository.existsBySerieAndNumeroTemporada(temporada.getSerie(), updateDTO.getNumeroTemporada())) {
                throw new RuntimeException("Já existe uma temporada com o número " + updateDTO.getNumeroTemporada());
            }
            temporada.setNumeroTemporada(updateDTO.getNumeroTemporada());
        }
        if (updateDTO.getAnoLancamento() != null) {
            temporada.setAnoLancamento(updateDTO.getAnoLancamento());
        }

        Temporada updatedTemporada = temporadaRepository.save(temporada);
        updateSerieStats(temporada.getSerie());

        return updatedTemporada;
    }

    @Transactional
    public void deleteTemporada(Long temporadaId) {
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada"));

        Serie serie = temporada.getSerie();
        temporadaRepository.delete(temporada);
        updateSerieStats(serie);
    }

    public List<Temporada> getTemporadasBySerie(Long serieId) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));
        return temporadaRepository.findBySerieOrderByNumeroTemporadaAsc(serie);
    }

    // ==================== GERENCIAMENTO DE EPISÓDIOS ====================

    @Transactional
    public Episodio createEpisodio(Long temporadaId, CreateEpisodioDTO createDTO) {
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada"));

        // Verificar se já existe episódio com esse número
        if (episodioRepository.existsByTemporadaAndNumeroEpisodio(temporada, createDTO.getNumeroEpisodio())) {
            throw new RuntimeException("Já existe um episódio com o número " + createDTO.getNumeroEpisodio());
        }

        Episodio episodio = new Episodio();
        episodio.setSerie(temporada.getSerie());
        episodio.setTemporada(temporada);
        episodio.setNumeroEpisodio(createDTO.getNumeroEpisodio());
        episodio.setTitle(createDTO.getTitle());
        episodio.setSinopse(createDTO.getSinopse());
        episodio.setDuracaoMinutos(createDTO.getDuracaoMinutos());
        episodio.setEmbed1(createDTO.getEmbed1());
        episodio.setEmbed2(createDTO.getEmbed2());
        episodio.setDataCadastro(new Date());

        Episodio savedEpisodio = episodioRepository.save(episodio);
        updateTemporadaStats(temporada);
        updateSerieStats(temporada.getSerie());

        return savedEpisodio;
    }

    @Transactional
    public Episodio updateEpisodio(Long episodioId, UpdateEpisodioDTO updateDTO) {
        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episódio não encontrado"));

        if (updateDTO.getNumeroEpisodio() != null) {
            // Verificar se novo número não conflita com existente
            if (!episodio.getNumeroEpisodio().equals(updateDTO.getNumeroEpisodio()) &&
                    episodioRepository.existsByTemporadaAndNumeroEpisodio(episodio.getTemporada(), updateDTO.getNumeroEpisodio())) {
                throw new RuntimeException("Já existe um episódio com o número " + updateDTO.getNumeroEpisodio());
            }
            episodio.setNumeroEpisodio(updateDTO.getNumeroEpisodio());
        }
        if (updateDTO.getTitle() != null) {
            episodio.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getSinopse() != null) {
            episodio.setSinopse(updateDTO.getSinopse());
        }
        if (updateDTO.getDuracaoMinutos() != null) {
            episodio.setDuracaoMinutos(updateDTO.getDuracaoMinutos());
        }
        if (updateDTO.getEmbed1() != null) {
            episodio.setEmbed1(updateDTO.getEmbed1());
        }
        if (updateDTO.getEmbed2() != null) {
            episodio.setEmbed2(updateDTO.getEmbed2());
        }

        Episodio updatedEpisodio = episodioRepository.save(episodio);
        updateTemporadaStats(episodio.getTemporada());
        updateSerieStats(episodio.getSerie());

        return updatedEpisodio;
    }

    @Transactional
    public void deleteEpisodio(Long episodioId) {
        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episódio não encontrado"));

        Temporada temporada = episodio.getTemporada();
        Serie serie = episodio.getSerie();

        episodioRepository.delete(episodio);
        updateTemporadaStats(temporada);
        updateSerieStats(serie);
    }

    public List<Episodio> getEpisodiosByTemporada(Long temporadaId) {
        Temporada temporada = temporadaRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada"));
        return episodioRepository.findByTemporadaOrderByNumeroEpisodioAsc(temporada);
    }

    public List<Episodio> getEpisodiosBySerie(Long serieId) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));
        return episodioRepository.findBySerieOrderByTemporadaAndEpisodio(serie);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void updateTemporadaStats(Temporada temporada) {
        int totalEpisodios = (int) episodioRepository.countByTemporada(temporada);
        temporada.setTotalEpisodios(totalEpisodios);
        temporadaRepository.save(temporada);
    }

    private void updateSerieStats(Serie serie) {
        int totalTemporadas = (int) temporadaRepository.countBySerie(serie);
        int totalEpisodios = (int) episodioRepository.countBySerie(serie);

        serie.setTotalTemporadas(totalTemporadas);
        serie.setTotalEpisodios(totalEpisodios);
        serieRepository.save(serie);
    }

    @Transactional
    public SerieCompleteDTO createSerieComplete(CreateSerieCompleteDTO createDTO) {
        try {
            // 1. Criar a série principal
            Serie serie = new Serie();
            serie.setTitle(createDTO.getTitle());
            serie.setSinopse(createDTO.getSinopse());
            serie.setCategoria(createDTO.getCategoria());
            serie.setAnoLancamento(createDTO.getAnoLancamento()); // MOVER PARA ANTES DO SAVE
            serie.setPaisOrigem(createDTO.getPaisOrigen());
            serie.setTmdbId(createDTO.getTmdbId());
            serie.setImdbId(createDTO.getImdbId());
            serie.setTrailer(createDTO.getTrailer());
            serie.setAvaliacao(createDTO.getAvaliacao());
            serie.setMinAge(createDTO.getMinAge());

            // DEFINIR TODAS AS URLs DAS IMAGENS ANTES DO SAVE
            serie.setPosterURL1(createDTO.getPosterURL1());
            serie.setPosterURL2(createDTO.getPosterURL2());
            serie.setLogoURL1(createDTO.getLogoURL1());
            serie.setLogoURL2(createDTO.getLogoURL2());
            serie.setBackdropURL1(createDTO.getBackdropURL1());
            serie.setBackdropURL2(createDTO.getBackdropURL2());
            serie.setBackdropURL3(createDTO.getBackdropURL3());
            serie.setBackdropURL4(createDTO.getBackdropURL4());

            serie.setDataCadastro(new Date());

            // Salvar série primeiro para obter o ID
            Serie savedSerie = serieRepository.save(serie);

            // 2. Criar temporadas e episódios
            int totalTemporadas = createDTO.getTemporadas().size();
            int totalEpisodios = 0;

            for (CreateSerieCompleteDTO.CreateTemporadaCompleteDTO temporadaDTO : createDTO.getTemporadas()) {
                // Criar temporada
                Temporada temporada = new Temporada();
                temporada.setSerie(savedSerie);
                temporada.setNumeroTemporada(temporadaDTO.getNumeroTemporada());
                temporada.setAnoLancamento(temporadaDTO.getAnoLancamento());
                temporada.setDataCadastro(new Date());

                // Salvar temporada
                Temporada savedTemporada = temporadaRepository.save(temporada);
                totalTemporadas++;

                // 3. Criar episódios da temporada
                int episodiosTemporada = 0;

                for (CreateSerieCompleteDTO.CreateEpisodioCompleteDTO episodioDTO : temporadaDTO.getEpisodios()) {
                    Episodio episodio = new Episodio();
                    episodio.setSerie(savedSerie);
                    episodio.setTemporada(savedTemporada);
                    episodio.setNumeroEpisodio(episodioDTO.getNumeroEpisodio());
                    episodio.setTitle(episodioDTO.getTitle());
                    episodio.setSinopse(episodioDTO.getSinopse());
                    episodio.setDuracaoMinutos(episodioDTO.getDuracaoMinutos());
                    episodio.setEmbed1(episodioDTO.getEmbed1());
                    episodio.setEmbed2(episodioDTO.getEmbed2());
                    episodio.setDataCadastro(new Date());

                    // Salvar episódio
                    episodioRepository.save(episodio);
                    episodiosTemporada++;
                    totalEpisodios++;
                }

                // Atualizar total de episódios da temporada
                savedTemporada.setTotalEpisodios(episodiosTemporada);
                temporadaRepository.save(savedTemporada);
            }

            // 4. Atualizar estatísticas da série
            savedSerie.setTotalTemporadas(totalTemporadas);
            savedSerie.setTotalEpisodios(totalEpisodios);
            Serie finalSerie = serieRepository.save(savedSerie);

            // 5. Retornar DTO completo usando o mapper
            return serieMapper.convertToCompleteDTO(finalSerie, null);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar série completa: " + e.getMessage(), e);
        }
    }
}