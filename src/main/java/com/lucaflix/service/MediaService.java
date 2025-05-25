package com.lucaflix.service;

import com.lucaflix.dto.media.MediaDTO;
import com.lucaflix.dto.media.MediaMapper;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final FilmeRepository filmeRepository;
    private final SerieRepository serieRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final LikeRepository likeRepository;
    private final EpisodioRepository episodioRepository;
    private final MediaMapper mediaMapper;

    /// BUSCA FILME POR ID PARA SINGLE PAGE
    public MediaDTO.FilmeDetailsResponse getFilmeById(Long filmeId) {
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new EntityNotFoundException("Filme não encontrado com id: " + filmeId));

        long totalLikes = likeRepository.countByFilme(filme);

        log.info("Filme {} buscado com sucesso", filmeId);
        return mediaMapper.toFilmeDetailsResponse(filme, totalLikes);
    }

    /// BUSCA SERIE COM TEMPORADAS E EPISODIOS PARA SINGLE PAGE
    public MediaDTO.SerieDetailsResponse getSerieById(Long serieId) {
        Serie serie = serieRepository.findByIdWithTemporadasAndEpisodios(serieId)
                .orElseThrow(() -> new EntityNotFoundException("Série não encontrada com id: " + serieId));

        long totalLikes = likeRepository.countBySerie(serie);

        log.info("Série {} buscada com sucesso", serieId);
        return mediaMapper.toSerieDetailsResponse(serie, totalLikes);
    }

    /// BUSCA TOP 10 SERIES COM MAIS LIKES
    public List<MediaDTO.SerieResponse> getTop10SeriesByLikes() {
        Pageable top10 = PageRequest.of(0, 10);
        List<Serie> topSeries = serieRepository.findTop10SeriesByLikes(top10);

        log.info("Top 10 séries por likes buscadas com sucesso");
        return topSeries.stream()
                .map(serie -> {
                    long totalLikes = likeRepository.countBySerie(serie);
                    return mediaMapper.toSerieResponse(serie, totalLikes);
                })
                .collect(Collectors.toList());
    }

    /// BUSCA TOP 10 FILMES COM MAIS LIKES
    public List<MediaDTO.FilmeResponse> getTop10FilmesByLikes() {
        Pageable top10 = PageRequest.of(0, 10);
        List<Filme> topFilmes = filmeRepository.findTop10FilmesByLikes(top10);

        log.info("Top 10 filmes por likes buscados com sucesso");
        return topFilmes.stream()
                .map(filme -> {
                    long totalLikes = likeRepository.countByFilme(filme);
                    return mediaMapper.toFilmeResponse(filme, totalLikes);
                })
                .collect(Collectors.toList());
    }

    /// BUSCA SERIES E FILMES QUE O USUARIO MARCOU COMO ASSISTIDO OU ESTA ASSISTINDO
    public MediaDTO.UserWatchedContentResponse getUserWatchedContent(User user) {
        List<MinhaLista> watchedMovies = minhaListaRepository.findByUserAndFilmeIsNotNullAndAssistidoTrueOrderByDataUltimaVisualizacaoDesc(user);
        List<MinhaLista> watchingSeries = minhaListaRepository.findByUserAndSerieIsNotNullOrderByDataUltimaVisualizacaoDesc(user);

        List<MediaDTO.FilmeResponse> filmesAssistidos = watchedMovies.stream()
                .map(item -> {
                    long totalLikes = likeRepository.countByFilme(item.getFilme());
                    return mediaMapper.toFilmeResponse(item.getFilme(), totalLikes);
                })
                .collect(Collectors.toList());

        List<MediaDTO.SerieResponse> seriesAssistindo = watchingSeries.stream()
                .map(item -> {
                    long totalLikes = likeRepository.countBySerie(item.getSerie());
                    return mediaMapper.toSerieResponse(item.getSerie(), totalLikes);
                })
                .collect(Collectors.toList());

        log.info("Conteúdo assistido do usuário {} buscado com sucesso", user.getId());
        return MediaDTO.UserWatchedContentResponse.builder()
                .filmesAssistidos(filmesAssistidos)
                .seriesAssistindo(seriesAssistindo)
                .build();
    }

    /// BUSCA MINHA LISTA DO USUARIO
    public MediaDTO.MinhaListaResponse getMinhaLista(User user) {
        List<MinhaLista> minhaLista = minhaListaRepository.findByUserOrderByDataAdicaoDesc(user);

        List<MediaDTO.FilmeResponse> filmes = minhaLista.stream()
                .filter(item -> item.getFilme() != null)
                .map(item -> {
                    long totalLikes = likeRepository.countByFilme(item.getFilme());
                    return mediaMapper.toFilmeResponse(item.getFilme(), totalLikes);
                })
                .collect(Collectors.toList());

        List<MediaDTO.SerieResponse> series = minhaLista.stream()
                .filter(item -> item.getSerie() != null)
                .map(item -> {
                    long totalLikes = likeRepository.countBySerie(item.getSerie());
                    return mediaMapper.toSerieResponse(item.getSerie(), totalLikes);
                })
                .collect(Collectors.toList());

        log.info("Minha lista do usuário {} buscada com sucesso", user.getId());
        return MediaDTO.MinhaListaResponse.builder()
                .filmes(filmes)
                .series(series)
                .build();
    }

    /// FILTRA CONTEUDO POR TIPO (SERIE/FILME) E CATEGORIA
    public MediaDTO.FilteredContentResponse filterContent(String tipo, Categoria categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<MediaDTO.FilmeResponse> filmes = null;
        List<MediaDTO.SerieResponse> series = null;

        if ("filme".equalsIgnoreCase(tipo) || "todos".equalsIgnoreCase(tipo)) {
            List<Filme> filmesList;
            if (categoria != null) {
                filmesList = filmeRepository.findByCategoriaOrderByDataCadastroDesc(categoria, pageable);
            } else {
                filmesList = filmeRepository.findAllByOrderByDataCadastroDesc(pageable);
            }

            filmes = filmesList.stream()
                    .map(filme -> {
                        long totalLikes = likeRepository.countByFilme(filme);
                        return mediaMapper.toFilmeResponse(filme, totalLikes);
                    })
                    .collect(Collectors.toList());
        }

        if ("serie".equalsIgnoreCase(tipo) || "todos".equalsIgnoreCase(tipo)) {
            List<Serie> seriesList;
            if (categoria != null) {
                seriesList = serieRepository.findByCategoriaOrderByDataCadastroDesc(categoria, pageable);
            } else {
                seriesList = serieRepository.findAllByOrderByDataCadastroDesc(pageable);
            }

            series = seriesList.stream()
                    .map(serie -> {
                        long totalLikes = likeRepository.countBySerie(serie);
                        return mediaMapper.toSerieResponse(serie, totalLikes);
                    })
                    .collect(Collectors.toList());
        }

        log.info("Conteúdo filtrado por tipo: {}, categoria: {}", tipo, categoria);
        return MediaDTO.FilteredContentResponse.builder()
                .filmes(filmes)
                .series(series)
                .build();
    }

    /// BUSCA SERIES OU FILMES POR NOME OU PARTE DO NOME
    public MediaDTO.SearchResultResponse searchContent(String searchTerm) {
        List<Filme> filmes = filmeRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(searchTerm);
        List<Serie> series = serieRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(searchTerm);

        List<MediaDTO.FilmeResponse> filmesResponse = filmes.stream()
                .map(filme -> {
                    long totalLikes = likeRepository.countByFilme(filme);
                    return mediaMapper.toFilmeResponse(filme, totalLikes);
                })
                .collect(Collectors.toList());

        List<MediaDTO.SerieResponse> seriesResponse = series.stream()
                .map(serie -> {
                    long totalLikes = likeRepository.countBySerie(serie);
                    return mediaMapper.toSerieResponse(serie, totalLikes);
                })
                .collect(Collectors.toList());

        log.info("Busca por '{}' retornou {} filmes e {} séries", searchTerm, filmes.size(), series.size());
        return MediaDTO.SearchResultResponse.builder()
                .filmes(filmesResponse)
                .series(seriesResponse)
                .totalResults(filmes.size() + series.size())
                .build();
    }

    /// ADICIONA OU REMOVE LIKE DE FILME
    @Transactional
    public MediaDTO.LikeResponse toggleFilmeLike(User user, Long filmeId) {
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new EntityNotFoundException("Filme não encontrado com id: " + filmeId));

        Optional<Like> existingLike = likeRepository.findByUserAndFilme(user, filme);

        boolean liked;
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            liked = false;
            log.info("Like removido do filme {} pelo usuário {}", filmeId, user.getId());
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setFilme(filme);
            likeRepository.save(newLike);
            liked = true;
            log.info("Like adicionado ao filme {} pelo usuário {}", filmeId, user.getId());
        }

        long totalLikes = likeRepository.countByFilme(filme);

        return MediaDTO.LikeResponse.builder()
                .liked(liked)
                .totalLikes(totalLikes)
                .build();
    }

    /// ADICIONA OU REMOVE LIKE DE SERIE
    @Transactional
    public MediaDTO.LikeResponse toggleSerieLike(User user, Long serieId) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new EntityNotFoundException("Série não encontrada com id: " + serieId));

        Optional<Like> existingLike = likeRepository.findByUserAndSerie(user, serie);

        boolean liked;
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            liked = false;
            log.info("Like removido da série {} pelo usuário {}", serieId, user.getId());
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setSerie(serie);
            likeRepository.save(newLike);
            liked = true;
            log.info("Like adicionado à série {} pelo usuário {}", serieId, user.getId());
        }

        long totalLikes = likeRepository.countBySerie(serie);

        return MediaDTO.LikeResponse.builder()
                .liked(liked)
                .totalLikes(totalLikes)
                .build();
    }

    /// VERIFICA SE USUARIO JA CURTIU O FILME
    public boolean hasUserLikedFilme(User user, Long filmeId) {
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new EntityNotFoundException("Filme não encontrado com id: " + filmeId));

        return likeRepository.existsByUserAndFilme(user, filme);
    }

    /// VERIFICA SE USUARIO JA CURTIU A SERIE
    public boolean hasUserLikedSerie(User user, Long serieId) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new EntityNotFoundException("Série não encontrada com id: " + serieId));

        return likeRepository.existsByUserAndSerie(user, serie);
    }
}