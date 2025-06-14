package com.lucaflix.service;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.dto.media.SerieCompleteDTO;
import com.lucaflix.dto.media.SerieSimpleDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SerieService {

    private final SerieRepository serieRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;

    // Todas as séries
    public PaginatedResponseDTO<SerieSimpleDTO> getAllSeries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Serie> seriesPage = serieRepository.findAll(pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Top 10 mais curtidas
    public List<SerieSimpleDTO> getTop10MostLiked() {
        List<Serie> topSeries = serieRepository.findTop10ByLikes(PageRequest.of(0, 10));
        return topSeries.stream().map(this::convertToSimpleDTO).collect(Collectors.toList());
    }

    // Obter série por ID - MÉTODO ATUALIZADO PARA INCLUIR TEMPORADAS E EPISÓDIOS
    public SerieCompleteDTO getSerieById(Long serieId, UUID userId) {
        Serie serie = serieRepository.findByIdWithTemporadasAndEpisodios(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));
        return convertToCompleteDTO(serie, userId);
    }

    // Toggle Like - adiciona se não existir, remove se existir
    @Transactional
    public boolean toggleLike(UUID userId, Long serieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));

        Like existingLike = likeRepository.findByUserAndSerie(user, serie).orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            return false; // Removeu like
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setSerie(serie);
            likeRepository.save(like);
            return true; // Adicionou like
        }
    }

    // Toggle Minha Lista - adiciona se não existir, remove se existir
    @Transactional
    public boolean toggleMyList(UUID userId, Long serieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));

        MinhaLista existingItem = minhaListaRepository.findByUserAndSerie(user, serie).orElse(null);

        if (existingItem != null) {
            minhaListaRepository.delete(existingItem);
            return false; // Removeu da lista
        } else {
            MinhaLista minhaLista = new MinhaLista();
            minhaLista.setUser(user);
            minhaLista.setSerie(serie);
            minhaListaRepository.save(minhaLista);
            return true; // Adicionou à lista
        }
    }

    // Minha lista do usuário
    public PaginatedResponseDTO<SerieSimpleDTO> getMyList(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MinhaLista> myListPage = minhaListaRepository.findByUser(user, pageable);

        List<SerieSimpleDTO> seriesList = myListPage.getContent().stream()
                .filter(item -> item.getSerie() != null)
                .map(item -> convertToSimpleDTO(item.getSerie()))
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                seriesList,
                myListPage.getNumber(),
                myListPage.getTotalPages(),
                myListPage.getTotalElements(),
                myListPage.getSize(),
                myListPage.isFirst(),
                myListPage.isLast(),
                myListPage.hasNext(),
                myListPage.hasPrevious()
        );
    }

    // Séries por categoria
    public PaginatedResponseDTO<SerieSimpleDTO> getSeriesByCategory(String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Categoria cat = Categoria.valueOf(categoria.toUpperCase());
        Page<Serie> seriesPage = serieRepository.findByCategoria(cat, pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Séries populares
    public PaginatedResponseDTO<SerieSimpleDTO> getPopularSeries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Serie> seriesPage = serieRepository.findPopularSeries(pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Séries com avaliação alta
    public PaginatedResponseDTO<SerieSimpleDTO> getHighRatedSeries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Serie> seriesPage = serieRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Séries recentes
    public PaginatedResponseDTO<SerieSimpleDTO> getRecentSeries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "anoLancamento"));
        Page<Serie> seriesPage = serieRepository.findAll(pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Séries por ano
    public PaginatedResponseDTO<SerieSimpleDTO> getSeriesByYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Serie> seriesPage = serieRepository.findByYear(year, pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Séries similares
    public PaginatedResponseDTO<SerieSimpleDTO> getSimilarSeries(Long serieId, int page, int size) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Série não encontrada"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Serie> seriesPage = serieRepository.findSimilarSeries(serie.getCategoria(), serie.getId(), pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Recomendações
    public PaginatedResponseDTO<SerieSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Serie> seriesPage = serieRepository.findRecommendations(userId, pageable);
        return createPaginatedResponse(seriesPage);
    }

    // Conversores
    private SerieSimpleDTO convertToSimpleDTO(Serie serie) {
        SerieSimpleDTO dto = new SerieSimpleDTO();
        dto.setId(serie.getId());
        dto.setTitle(serie.getTitle());
        dto.setAnoLancamento(serie.getAnoLancamento());
        dto.setTmdbId(serie.getTmdbId());
        dto.setImdbId(serie.getImdbId());
        dto.setPaisOrigem(serie.getPaisOrigem());
        dto.setCategoria(serie.getCategoria());
        dto.setMinAge(serie.getMinAge());
        dto.setAvaliacao(serie.getAvaliacao());
        dto.setImageURL1(serie.getImageURL1());
        dto.setImageURL2(serie.getImageURL2());
        dto.setTotalTemporadas(serie.getTotalTemporadas());
        dto.setTotalEpisodios(serie.getTotalEpisodios());
        dto.setTotalLikes((long) (serie.getLikes() != null ? serie.getLikes().size() : 0));
        return dto;
    }

    // MÉTODO ATUALIZADO PARA INCLUIR TEMPORADAS E EPISÓDIOS
    private SerieCompleteDTO convertToCompleteDTO(Serie serie, UUID userId) {
        SerieCompleteDTO dto = new SerieCompleteDTO();
        dto.setId(serie.getId());
        dto.setTitle(serie.getTitle());
        dto.setAnoLancamento(serie.getAnoLancamento());
        dto.setTmdbId(serie.getTmdbId());
        dto.setImdbId(serie.getImdbId());
        dto.setPaisOrigem(serie.getPaisOrigem());
        dto.setSinopse(serie.getSinopse());
        dto.setDataCadastro(serie.getDataCadastro());
        dto.setCategoria(serie.getCategoria());
        dto.setMinAge(serie.getMinAge());
        dto.setAvaliacao(serie.getAvaliacao());
        dto.setTrailer(serie.getTrailer());
        dto.setImageURL1(serie.getImageURL1());
        dto.setImageURL2(serie.getImageURL2());
        dto.setTotalTemporadas(serie.getTotalTemporadas());
        dto.setTotalEpisodios(serie.getTotalEpisodios());
        dto.setTotalLikes((long) (serie.getLikes() != null ? serie.getLikes().size() : 0));

        // Converter temporadas e episódios
        if (serie.getTemporadas() != null) {
            List<SerieCompleteDTO.TemporadaDTO> temporadasDTO = serie.getTemporadas().stream()
                    .sorted((t1, t2) -> t1.getNumeroTemporada().compareTo(t2.getNumeroTemporada()))
                    .map(this::convertToTemporadaDTO)
                    .collect(Collectors.toList());
            dto.setTemporadas(temporadasDTO);
        }

        // Verificar se usuário curtiu e se está na lista
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                dto.setUserLiked(likeRepository.existsByUserAndSerie(user, serie));
                dto.setInUserList(minhaListaRepository.existsByUserAndSerie(user, serie));
            }
        } else if (userId == null) {
            dto.setInUserList(null);
            dto.setUserLiked(null);

        }

        return dto;
    }

    // NOVO MÉTODO PARA CONVERTER TEMPORADA
    private SerieCompleteDTO.TemporadaDTO convertToTemporadaDTO(Temporada temporada) {
        SerieCompleteDTO.TemporadaDTO dto = new SerieCompleteDTO.TemporadaDTO();
        dto.setId(temporada.getId());
        dto.setNumeroTemporada(temporada.getNumeroTemporada());
        dto.setAnoLancamento(temporada.getAnoLancamento());
        dto.setDataCadastro(temporada.getDataCadastro());
        dto.setTotalEpisodios(temporada.getTotalEpisodios());

        // Converter episódios
        if (temporada.getEpisodios() != null) {
            List<SerieCompleteDTO.EpisodioDTO> episodiosDTO = temporada.getEpisodios().stream()
                    .sorted((e1, e2) -> e1.getNumeroEpisodio().compareTo(e2.getNumeroEpisodio()))
                    .map(this::convertToEpisodioDTO)
                    .collect(Collectors.toList());
            dto.setEpisodios(episodiosDTO);
        }

        return dto;
    }

    // NOVO MÉTODO PARA CONVERTER EPISÓDIO
    private SerieCompleteDTO.EpisodioDTO convertToEpisodioDTO(Episodio episodio) {
        SerieCompleteDTO.EpisodioDTO dto = new SerieCompleteDTO.EpisodioDTO();
        dto.setId(episodio.getId());
        dto.setNumeroEpisodio(episodio.getNumeroEpisodio());
        dto.setTitle(episodio.getTitle());
        dto.setSinopse(episodio.getSinopse());
        dto.setDuracaoMinutos(episodio.getDuracaoMinutos());
        dto.setDataCadastro(episodio.getDataCadastro());
        dto.setEmbed1(episodio.getEmbed1());
        dto.setEmbed2(episodio.getEmbed2());
        return dto;
    }

    private PaginatedResponseDTO<SerieSimpleDTO> createPaginatedResponse(Page<Serie> seriesPage) {
        List<SerieSimpleDTO> seriesDTOs = seriesPage.getContent().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                seriesDTOs,
                seriesPage.getNumber(),
                seriesPage.getTotalPages(),
                seriesPage.getTotalElements(),
                seriesPage.getSize(),
                seriesPage.isFirst(),
                seriesPage.isLast(),
                seriesPage.hasNext(),
                seriesPage.hasPrevious()
        );
    }
}