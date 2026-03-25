package com.lucaflix.service;

import com.lucaflix.dto.request.serie.*;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.mapper.SerieMapper;
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
    private final TempRepository tempRepository;
    private final EpisodioRepository episodioRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final SerieMapper serieMapper;

    // ==================== GERENCIAMENTO DE SÉRIES ====================

    @Transactional
    public SerieCompleteDTO createSerie(CreateSerieDTO createDTO) {
        Series series = new Series();
        mapCreateDTOToSerie(createDTO, series);
        series.setDataCadastro(new Date());

        Series savedSeries = serieRepository.save(series);
        return serieMapper.convertToCompleteDTO(savedSeries, null);
    }

    @Transactional
    public SerieCompleteDTO updateSerie(Long serieId, UpdateSerieDTO updateDTO) {
        Series series = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada com ID: " + serieId));

        mapUpdateDTOToSerie(updateDTO, series);

        Series updatedSeries = serieRepository.save(series);
        return serieMapper.convertToCompleteDTO(updatedSeries, null);
    }

    @Transactional
    public void deleteSerie(Long serieId) {
        Series series = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada com ID: " + serieId));

        // Deletar relacionamentos primeiro
        likeRepository.deleteBySerie(series);
        minhaListaRepository.deleteBySerie(series);

        // Deletar série (cascade deletará temporadas e episódios)
        serieRepository.delete(series);
    }

    public SerieCompleteDTO getSerieById(Long serieId) {
        Series series = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada com ID: " + serieId));
        return serieMapper.convertToCompleteDTO(series, null);
    }

    // ==================== GERENCIAMENTO DE TEMPORADAS ====================

    @Transactional
    public Season createTemporada(Long serieId, CreateTemporadaDTO createDTO) {
        Series series = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada com ID: " + serieId));

        // Verificar se já existe temporada com esse número
        if (tempRepository.existsBySerieAndNumeroTemporada(series, createDTO.getNumeroTemporada())) {
            throw new RuntimeException("Já existe uma temporada com o número " + createDTO.getNumeroTemporada() + " para esta série");
        }

        Season season = new Season();
        season.setSeries(series);
        season.setNumeroTemporada(createDTO.getNumeroTemporada());
        season.setAnoLancamento(createDTO.getAnoLancamento());
        season.setDataCadastro(new Date());
        season.setTotalEpisodios(0);

        Season savedSeason = tempRepository.save(season);
        updateSerieStats(series);

        return savedSeason;
    }

    @Transactional
    public Season updateTemporada(Long temporadaId, UpdateTemporadaDTO updateDTO) {
        Season season = tempRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada com ID: " + temporadaId));

        if (updateDTO.getNumeroTemporada() != null) {
            // Verificar se novo número não conflita com existente
            if (!season.getNumeroTemporada().equals(updateDTO.getNumeroTemporada()) &&
                    tempRepository.existsBySerieAndNumeroTemporada(season.getSeries(), updateDTO.getNumeroTemporada())) {
                throw new RuntimeException("Já existe uma temporada com o número " + updateDTO.getNumeroTemporada() + " para esta série");
            }
            season.setNumeroTemporada(updateDTO.getNumeroTemporada());
        }
        if (updateDTO.getAnoLancamento() != null) {
            season.setAnoLancamento(updateDTO.getAnoLancamento());
        }

        Season updatedSeason = tempRepository.save(season);
        updateSerieStats(season.getSeries());

        return updatedSeason;
    }

    @Transactional
    public void deleteTemporada(Long temporadaId) {
        Season season = tempRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada com ID: " + temporadaId));

        Series series = season.getSeries();
        tempRepository.delete(season);
        updateSerieStats(series);
    }

    public List<Season> getTemporadasBySerie(Long serieId) {
        Series series = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada com ID: " + serieId));
        return tempRepository.findBySerieOrderByNumeroTemporadaAsc(series);
    }

    // ==================== GERENCIAMENTO DE EPISÓDIOS ====================

    @Transactional
    public Episode createEpisodio(Long temporadaId, CreateEpisodioDTO createDTO) {
        Season season = tempRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada com ID: " + temporadaId));

        // Verificar se já existe episódio com esse número
        if (episodioRepository.existsByTemporadaAndNumeroEpisodio(season, createDTO.getNumeroEpisodio())) {
            throw new RuntimeException("Já existe um episódio com o número " + createDTO.getNumeroEpisodio() + " nesta temporada");
        }

        Episode episode = new Episode();
        episode.setSerie(season.getSeries());
        episode.setSeason(season);
        episode.setNumeroEpisodio(createDTO.getNumeroEpisodio());
        episode.setTitle(createDTO.getTitle());
        episode.setSinopse(createDTO.getSinopse());
        episode.setDuracaoMinutos(createDTO.getDuracaoMinutos() != null ? createDTO.getDuracaoMinutos() : 0);
        episode.setEmbed1(createDTO.getEmbed1());
        episode.setEmbed2(createDTO.getEmbed2());
        episode.setDataCadastro(new Date());

        Episode savedEpisode = episodioRepository.save(episode);
        updateTemporadaStats(season);
        updateSerieStats(season.getSeries());

        return savedEpisode;
    }

    @Transactional
    public Episode updateEpisodio(Long episodioId, UpdateEpisodioDTO updateDTO) {
        Episode episode = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episódio não encontrado com ID: " + episodioId));

        if (updateDTO.getNumeroEpisodio() != null) {
            // Verificar se novo número não conflita com existente
            if (!episode.getNumeroEpisodio().equals(updateDTO.getNumeroEpisodio()) &&
                    episodioRepository.existsByTemporadaAndNumeroEpisodio(episode.getSeason(), updateDTO.getNumeroEpisodio())) {
                throw new RuntimeException("Já existe um episódio com o número " + updateDTO.getNumeroEpisodio() + " nesta temporada");
            }
            episode.setNumeroEpisodio(updateDTO.getNumeroEpisodio());
        }
        if (updateDTO.getTitle() != null) {
            episode.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getSinopse() != null) {
            episode.setSinopse(updateDTO.getSinopse());
        }
        if (updateDTO.getDuracaoMinutos() != null) {
            episode.setDuracaoMinutos(updateDTO.getDuracaoMinutos());
        }
        if (updateDTO.getEmbed1() != null) {
            episode.setEmbed1(updateDTO.getEmbed1());
        }
        if (updateDTO.getEmbed2() != null) {
            episode.setEmbed2(updateDTO.getEmbed2());
        }

        Episode updatedEpisode = episodioRepository.save(episode);
        updateTemporadaStats(episode.getSeason());
        updateSerieStats(episode.getSerie());

        return updatedEpisode;
    }

    @Transactional
    public void deleteEpisodio(Long episodioId) {
        Episode episode = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new RuntimeException("Episódio não encontrado com ID: " + episodioId));

        Season season = episode.getSeason();
        Series series = episode.getSerie();

        episodioRepository.delete(episode);
        updateTemporadaStats(season);
        updateSerieStats(series);
    }

    public List<Episode> getEpisodiosByTemporada(Long temporadaId) {
        Season season = tempRepository.findById(temporadaId)
                .orElseThrow(() -> new RuntimeException("Temporada não encontrada com ID: " + temporadaId));
        return episodioRepository.findByTemporadaOrderByNumeroEpisodioAsc(season);
    }

    public List<Episode> getEpisodiosBySerie(Long serieId) {
        Series series = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada com ID: " + serieId));
        return episodioRepository.findBySerieOrderByTemporadaAndEpisodio(series);
    }

    // ==================== CRIAÇÃO COMPLETA ====================

    @Transactional
    public SerieCompleteDTO createSerieComplete(CreateSerieCompleteDTO createDTO) {
        try {
            // 1. Criar a série principal
            Series series = new Series();
            series.setTitle(createDTO.getTitle());
            series.setSinopse(createDTO.getSinopse());
            series.setCategoria(createDTO.getCategories());
            series.setAnoLancamento(createDTO.getAnoLancamento());
            series.setPaisOrigem(createDTO.getPaisOrigen());
            series.setTmdbId(createDTO.getTmdbId());
            series.setImdbId(createDTO.getImdbId());
            series.setTrailer(createDTO.getTrailer());
            series.setAvaliacao(createDTO.getAvaliacao());
            series.setMinAge(createDTO.getMinAge());
            series.setPosterURL1(createDTO.getPosterURL1());
            series.setPosterURL2(createDTO.getPosterURL2());
            series.setLogoURL1(createDTO.getLogoURL1());
            series.setLogoURL2(createDTO.getLogoURL2());
            series.setBackdropURL1(createDTO.getBackdropURL1());
            series.setBackdropURL2(createDTO.getBackdropURL2());
            series.setBackdropURL3(createDTO.getBackdropURL3());
            series.setBackdropURL4(createDTO.getBackdropURL4());
            series.setDataCadastro(new Date());
            series.setTotalTemporadas(0);
            series.setTotalEpisodios(0);

            // Salvar série primeiro
            Series savedSeries = serieRepository.save(series);

            // 2. Criar temporadas e episódios
            int totalTemporadas = 0;
            int totalEpisodios = 0;

            if (createDTO.getTemporadas() != null) {
                for (CreateSerieCompleteDTO.CreateTemporadaCompleteDTO temporadaDTO : createDTO.getTemporadas()) {
                    // Criar temporada
                    Season season = new Season();
                    season.setSeries(savedSeries);
                    season.setNumeroTemporada(temporadaDTO.getNumeroTemporada());
                    season.setAnoLancamento(temporadaDTO.getAnoLancamento());
                    season.setDataCadastro(new Date());
                    season.setTotalEpisodios(0);

                    Season savedSeason = tempRepository.save(season);
                    totalTemporadas++;

                    // 3. Criar episódios da temporada
                    int episodiosTemporada = 0;

                    if (temporadaDTO.getEpisodios() != null) {
                        for (CreateSerieCompleteDTO.CreateEpisodioCompleteDTO episodioDTO : temporadaDTO.getEpisodios()) {
                            Episode episode = new Episode();
                            episode.setSerie(savedSeries);
                            episode.setSeason(savedSeason);
                            episode.setNumeroEpisodio(episodioDTO.getNumeroEpisodio());
                            episode.setTitle(episodioDTO.getTitle());
                            episode.setSinopse(episodioDTO.getSinopse());
                            episode.setDuracaoMinutos(episodioDTO.getDuracaoMinutos() != null ? episodioDTO.getDuracaoMinutos() : 0);
                            episode.setEmbed1(episodioDTO.getEmbed1());
                            episode.setEmbed2(episodioDTO.getEmbed2());
                            episode.setDataCadastro(new Date());

                            episodioRepository.save(episode);
                            episodiosTemporada++;
                            totalEpisodios++;
                        }
                    }

                    // Atualizar total de episódios da temporada
                    savedSeason.setTotalEpisodios(episodiosTemporada);
                    tempRepository.save(savedSeason);
                }
            }

            // 4. Atualizar estatísticas da série
            savedSeries.setTotalTemporadas(totalTemporadas);
            savedSeries.setTotalEpisodios(totalEpisodios);
            Series finalSeries = serieRepository.save(savedSeries);

            // 5. Retornar DTO completo
            return serieMapper.convertToCompleteDTO(finalSeries, null);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar série completa: " + e.getMessage(), e);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void updateTemporadaStats(Season season) {
        try {
            int totalEpisodios = (int) episodioRepository.countByTemporada(season);
            season.setTotalEpisodios(totalEpisodios);
            tempRepository.save(season);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Erro ao atualizar estatísticas da temporada: " + e.getMessage());
        }
    }

    private void updateSerieStats(Series series) {
        try {
            int totalTemporadas = (int) tempRepository.countBySerie(series);
            int totalEpisodios = (int) episodioRepository.countBySerie(series);

            series.setTotalTemporadas(totalTemporadas);
            series.setTotalEpisodios(totalEpisodios);
            serieRepository.save(series);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Erro ao atualizar estatísticas da série: " + e.getMessage());
        }
    }

    private void mapCreateDTOToSerie(CreateSerieDTO createDTO, Series series) {
        series.setTitle(createDTO.getTitle());
        series.setAnoLancamento(createDTO.getAnoLancamento());
        series.setTmdbId(createDTO.getTmdbId());
        series.setImdbId(createDTO.getImdbId());
        series.setPaisOrigem(createDTO.getPaisOrigen());
        series.setSinopse(createDTO.getSinopse());
        series.setCategoria(createDTO.getCategories());
        series.setMinAge(createDTO.getMinAge());
        series.setAvaliacao(createDTO.getAvaliacao());
        series.setTrailer(createDTO.getTrailer());
        series.setPosterURL1(createDTO.getPosterURL1());
        series.setPosterURL2(createDTO.getPosterURL2());
        series.setLogoURL1(createDTO.getLogoURL1());
        series.setLogoURL2(createDTO.getLogoURL2());
        series.setBackdropURL1(createDTO.getBackdropURL1());
        series.setBackdropURL2(createDTO.getBackdropURL2());
        series.setBackdropURL3(createDTO.getBackdropURL3());
        series.setBackdropURL4(createDTO.getBackdropURL4());
        series.setTotalTemporadas(0);
        series.setTotalEpisodios(0);
    }

    private void mapUpdateDTOToSerie(UpdateSerieDTO updateDTO, Series series) {
        if (updateDTO.getTitle() != null) series.setTitle(updateDTO.getTitle());
        if (updateDTO.getAnoLancamento() != null) series.setAnoLancamento(updateDTO.getAnoLancamento());
        if (updateDTO.getTmdbId() != null) series.setTmdbId(updateDTO.getTmdbId());
        if (updateDTO.getImdbId() != null) series.setImdbId(updateDTO.getImdbId());
        if (updateDTO.getPaisOrigen() != null) series.setPaisOrigem(updateDTO.getPaisOrigen());
        if (updateDTO.getSinopse() != null) series.setSinopse(updateDTO.getSinopse());
        if (updateDTO.getCategories() != null) series.setCategoria(updateDTO.getCategories());
        if (updateDTO.getMinAge() != null) series.setMinAge(updateDTO.getMinAge());
        if (updateDTO.getAvaliacao() != null) series.setAvaliacao(updateDTO.getAvaliacao());
        if (updateDTO.getTrailer() != null) series.setTrailer(updateDTO.getTrailer());
        if (updateDTO.getLogoURL1() != null) series.setLogoURL1(updateDTO.getLogoURL1());
        if (updateDTO.getLogoURL2() != null) series.setLogoURL2(updateDTO.getLogoURL2()); // CORRIGIDO
        if (updateDTO.getBackdropURL1() != null) series.setBackdropURL1(updateDTO.getBackdropURL1());
        if (updateDTO.getBackdropURL2() != null) series.setBackdropURL2(updateDTO.getBackdropURL2());
        if (updateDTO.getBackdropURL3() != null) series.setBackdropURL3(updateDTO.getBackdropURL3());
        if (updateDTO.getBackdropURL4() != null) series.setBackdropURL4(updateDTO.getBackdropURL4());
        if (updateDTO.getPosterURL1() != null) series.setPosterURL1(updateDTO.getPosterURL1());
        if (updateDTO.getPosterURL2() != null) series.setPosterURL2(updateDTO.getPosterURL2());
    }
}