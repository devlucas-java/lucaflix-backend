package com.lucaflix.service;

import com.lucaflix.dto.media.MediaCompleteDTO;
import com.lucaflix.dto.media.MediaFilter;
import com.lucaflix.dto.media.MediaSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
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
public class MediaService {

    private final MediaRepository mediaRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;

    public PaginatedResponseDTO<MediaSimpleDTO> filtrarMedia(MediaFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));

        Page<Media> mediaPage = mediaRepository.buscarPorFiltros(
                filter.getIsFilme(),
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getAnoLancamentoInicio(),
                filter.getAnoLancamentoFim(),
                filter.getCategoria(),
                pageable
        );

        List<MediaSimpleDTO> mediaDTOs = mediaPage.getContent().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                mediaDTOs,
                mediaPage.getNumber(),
                mediaPage.getTotalPages(),
                mediaPage.getTotalElements(),
                mediaPage.getSize(),
                mediaPage.isFirst(),
                mediaPage.isLast(),
                mediaPage.hasNext(),
                mediaPage.hasPrevious()
        );
    }

    /**
     * Retorna as top 10 mídias com mais likes
     */
    public List<MediaSimpleDTO> getTop10MostLiked() {
        List<Media> topMedia = mediaRepository.findTop10ByLikes(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataCadastro")));
        return topMedia.stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna uma mídia completa por ID
     */
    public MediaCompleteDTO getMediaById(Long mediaId, UUID userId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        return convertToCompleteDTO(media, userId);
    }

    /**
     * Adiciona uma mídia à lista do usuário
     */
    @Transactional
    public void addToMyList(UUID userId, Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        // Verifica se já não está na lista
        boolean alreadyInList = minhaListaRepository.existsByUserAndMedia(user, media);
        if (alreadyInList) {
            throw new RuntimeException("Mídia já está na sua lista");
        }

        MinhaLista minhaLista = new MinhaLista();
        minhaLista.setUser(user);
        minhaLista.setMedia(media);

        minhaListaRepository.save(minhaLista);
    }

    /**
     * Remove uma mídia da lista do usuário
     */
    @Transactional
    public void removeFromMyList(UUID userId, Long mediaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        MinhaLista minhaLista = minhaListaRepository.findByUserAndMedia(user, media)
                .orElseThrow(() -> new RuntimeException("Mídia não está na sua lista"));

        minhaListaRepository.delete(minhaLista);
    }

    /**
     * Dá like em uma mídia
     */
    @Transactional
    public void likeMedia(UUID userId, Long likeDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Media media = mediaRepository.findById(likeDTO)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        // Verifica se já não deu like
        boolean alreadyLiked = likeRepository.existsByUserAndMedia(user, media);
        if (alreadyLiked) {
            throw new RuntimeException("Você já curtiu esta mídia");
        }

        Like like = new Like();
        like.setUser(user);
        like.setMedia(media);

        likeRepository.save(like);
    }

    /**
     * Remove like de uma mídia
     */
    @Transactional
    public void unlikeMedia(UUID userId, Long mediaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        Like like = likeRepository.findByUserAndMedia(user, media)
                .orElseThrow(() -> new RuntimeException("Você não curtiu esta mídia"));

        likeRepository.delete(like);
    }

    /**
     * Retorna a lista pessoal do usuário
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getMyList(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MinhaLista> myListPage = minhaListaRepository.findByUser(user, pageable);

        List<MediaSimpleDTO> mediaList = myListPage.getContent().stream()
                .map(minhaLista -> convertToSimpleDTO(minhaLista.getMedia()))
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                mediaList,
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

    /**
     * Mídias com avaliação alta (acima de 7.0)
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getHighRatedMedia(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Media> mediaPage = mediaRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Novos lançamentos - últimas mídias adicionadas
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getNewReleases(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Media> mediaPage = mediaRepository.findAll(pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Apenas filmes
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Media> mediaPage = mediaRepository.findByIsFilmeTrue(pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Apenas séries
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getSeries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Media> mediaPage = mediaRepository.findByIsFilmeFalse(pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Mídias por categoria - AJUSTADO para lista de categorias
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getMediaByCategory(String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Categoria cat = Categoria.valueOf(categoria.toUpperCase());
        Page<Media> mediaPage = mediaRepository.findByCategoria(cat, pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Mídias por múltiplas categorias - NOVO MÉTODO
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getMediaByCategories(List<String> categorias, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        List<Categoria> cats = categorias.stream()
                .map(cat -> Categoria.valueOf(cat.toUpperCase()))
                .collect(Collectors.toList());
        Page<Media> mediaPage = mediaRepository.findByCategoriaIn(cats, pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Filmes populares (mais curtidos)
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getPopularMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Media> mediaPage = mediaRepository.findPopularMovies(pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Séries populares (mais curtidas)
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getPopularSeries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Media> mediaPage = mediaRepository.findPopularSeries(pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Filmes recentes
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getRecentMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "anoLancamento"));
        Page<Media> mediaPage = mediaRepository.findByIsFilmeTrue(pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Séries recentes
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getRecentSeries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "anoLancamento"));
        Page<Media> mediaPage = mediaRepository.findByIsFilmeFalse(pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Mídias por faixa etária
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getMediaByAgeRating(String minAge, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Media> mediaPage = mediaRepository.findByMinAge(minAge, pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Mídias por duração
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getMediaByDuration(Integer minDuration, Integer maxDuration, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Media> mediaPage = mediaRepository.findByDurationRange(minDuration, maxDuration, pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Mídias por ano
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getMediaByYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Media> mediaPage = mediaRepository.findByYear(year, pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Recomendações baseadas no que o usuário curtiu
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Media> mediaPage = mediaRepository.findRecommendations(userId, pageable);

        return createPaginatedResponse(mediaPage);
    }

    /**
     * Continuar assistindo (lista do usuário)
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getContinueWatching(UUID userId, int page, int size) {
        return getMyList(userId, page, size); // Reutiliza a lista do usuário
    }

    /**
     * Mídias similares - AJUSTADO para lista de categorias
     */
    public PaginatedResponseDTO<MediaSimpleDTO> getSimilarMedia(Long mediaId, int page, int size) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Media> mediaPage = mediaRepository.findSimilarMedia(media.getCategoria(), media.getId(), pageable);

        return createPaginatedResponse(mediaPage);
    }

    // Métodos privados de conversão
    private MediaSimpleDTO convertToSimpleDTO(Media media) {
        MediaSimpleDTO dto = new MediaSimpleDTO();
        dto.setId(media.getId());
        dto.setTitle(media.getTitle());
        dto.setFilme(media.isFilme());
        dto.setAnoLancamento(media.getAnoLancamento());
        dto.setDuracaoMinutos(media.getDuracaoMinutos());
        dto.setTmdbId(media.getTmdbId());
        dto.setImdbId(media.getImdbId());
        dto.setPaisOrigen(media.getPaisOrigen());
        dto.setCategoria(media.getCategoria()); // Agora retorna lista
        dto.setMinAge(media.getMinAge());
        dto.setAvaliacao(media.getAvaliacao());
        dto.setImageURL(media.getImageURL());
        dto.setTotalLikes((long) (media.getLikes() != null ? media.getLikes().size() : 0));
        return dto;
    }

    private MediaCompleteDTO convertToCompleteDTO(Media media, UUID userId) {
        MediaCompleteDTO dto = new MediaCompleteDTO();
        dto.setId(media.getId());
        dto.setTitle(media.getTitle());
        dto.setFilme(media.isFilme());
        dto.setAnoLancamento(media.getAnoLancamento());
        dto.setDuracaoMinutos(media.getDuracaoMinutos());
        dto.setTmdbId(media.getTmdbId());
        dto.setImdbId(media.getImdbId());
        dto.setPaisOrigen(media.getPaisOrigen());
        dto.setSinopse(media.getSinopse());
        dto.setDataCadastro(media.getDataCadastro());
        dto.setCategoria(media.getCategoria()); // Agora retorna lista
        dto.setMinAge(media.getMinAge());
        dto.setAvaliacao(media.getAvaliacao());
        dto.setEmbed1(media.getEmbed1());
        dto.setEmbed2(media.getEmbed2());
        dto.setTrailer(media.getTrailer());
        dto.setImageURL(media.getImageURL());
        dto.setTotalLikes((long) (media.getLikes() != null ? media.getLikes().size() : 0));

        // Verifica se o usuário curtiu
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                dto.setUserLiked(likeRepository.existsByUserAndMedia(user, media));
                dto.setInUserList(minhaListaRepository.existsByUserAndMedia(user, media));
            }
        }

        return dto;
    }

    /**
     * Método auxiliar para criar resposta paginada
     */
    private PaginatedResponseDTO<MediaSimpleDTO> createPaginatedResponse(Page<Media> mediaPage) {
        List<MediaSimpleDTO> mediaDTOs = mediaPage.getContent().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                mediaDTOs,
                mediaPage.getNumber(),
                mediaPage.getTotalPages(),
                mediaPage.getTotalElements(),
                mediaPage.getSize(),
                mediaPage.isFirst(),
                mediaPage.isLast(),
                mediaPage.hasNext(),
                mediaPage.hasPrevious()
        );
    }

    /**
     * Busca uma única mídia por título e ano exatos
     */
    public MediaCompleteDTO buscarPorTituloEAno(String title, Integer year, UUID userId) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Título não pode ser vazio");
        }

        if (year == null) {
            throw new RuntimeException("Ano não pode ser nulo");
        }

        Media media = mediaRepository.findByTitleAndYear(title.trim(), year)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada com título '" + title + "' e ano " + year));

        return convertToCompleteDTO(media, userId);
    }
}