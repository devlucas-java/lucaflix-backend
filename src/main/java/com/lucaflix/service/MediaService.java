package com.lucaflix.service;

import com.lucaflix.dto.media.MediaCompleteDTO;
import com.lucaflix.dto.media.MediaFilter;
import com.lucaflix.dto.media.MediaSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.*;
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
        List<Media> mediaList = mediaRepository.buscarPorFiltros(
                filter.getIsFilme(),
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getAnoLancamentoInicio(),
                filter.getAnoLancamentoFim(),
                filter.getCategoria()
        );

        long totalElements = mediaRepository.contarPorFiltros(
                filter.getIsFilme(),
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getAnoLancamentoInicio(),
                filter.getAnoLancamentoFim(),
                filter.getCategoria()
        );

        // Paginação manual dos resultados
        int inicio = page * size;
        mediaList = mediaList.stream()
                .sorted((m1, m2) -> m2.getDataCadastro().compareTo(m1.getDataCadastro()))
                .skip(inicio)
                .limit(size)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                mediaList.stream()
                        .map(this::convertToSimpleDTO)
                        .collect(Collectors.toList()),
                page,
                (int) Math.ceil((double) totalElements / size),
                totalElements,
                size,
                page == 0,
                totalElements <= size,
                totalElements > (page + 1) * size,
                page > 0
        );
    }





    /**
     * Retorna as top 10 mídias com mais likes
     */
    public List<MediaSimpleDTO> getTop10MostLiked() {
        List<Media> topMedia = mediaRepository.findTop10ByOrderByLikesDesc();
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


    // Métodos privados de conversão
    private MediaSimpleDTO convertToSimpleDTO(Media media) {
        MediaSimpleDTO dto = new MediaSimpleDTO();
        dto.setId(media.getId());
        dto.setTitle(media.getTitle());
        dto.setFilme(media.isFilme());
        dto.setAnoLancamento(media.getAnoLancamento());
        dto.setDuracaoMinutos(media.getDuracaoMinutos());
        dto.setCategoria(media.getCategoria());
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
        dto.setSinopse(media.getSinopse());
        dto.setDataCadastro(media.getDataCadastro());
        dto.setCategoria(media.getCategoria());
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
}