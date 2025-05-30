package com.lucaflix.service;

import com.lucaflix.dto.admin.AdminMediaDTO;
import com.lucaflix.dto.admin.CreateMediaDTO;
import com.lucaflix.dto.admin.MediaStatsDTO;
import com.lucaflix.dto.admin.UpdateMediaDTO;
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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MediaRepository mediaRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;



    /**
     * Cria uma nova mídia
     */
    @Transactional
    public AdminMediaDTO createMedia( CreateMediaDTO createMediaDTO) {

        Media media = new Media();
        media.setTitle(createMediaDTO.getTitle());
        media.setFilme(createMediaDTO.getIsFilme());
        media.setAnoLancamento(createMediaDTO.getAnoLancamento());
        media.setDuracaoMinutos(createMediaDTO.getDuracaoMinutos());
        media.setSinopse(createMediaDTO.getSinopse());
        media.setCategoria(createMediaDTO.getCategoria());
        media.setMinAge(createMediaDTO.getMinAge());
        media.setAvaliacao(createMediaDTO.getAvaliacao());
        media.setEmbed1(createMediaDTO.getEmbed1());
        media.setEmbed2(createMediaDTO.getEmbed2());
        media.setTrailer(createMediaDTO.getTrailer());
        media.setImageURL(createMediaDTO.getImageURL());
        media.setDataCadastro(new Date());

        Media savedMedia = mediaRepository.save(media);
        return convertToAdminDTO(savedMedia);
    }

    /**
     * Atualiza uma mídia existente
     */
    @Transactional
    public AdminMediaDTO updateMedia( Long mediaId, UpdateMediaDTO updateMediaDTO) {

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        // Atualiza apenas campos não nulos
        if (updateMediaDTO.getTitle() != null) {
            media.setTitle(updateMediaDTO.getTitle());
        }
        if (updateMediaDTO.getIsFilme() != null) {
            media.setFilme(updateMediaDTO.getIsFilme());
        }
        if (updateMediaDTO.getAnoLancamento() != null) {
            media.setAnoLancamento(updateMediaDTO.getAnoLancamento());
        }
        if (updateMediaDTO.getDuracaoMinutos() != null) {
            media.setDuracaoMinutos(updateMediaDTO.getDuracaoMinutos());
        }
        if (updateMediaDTO.getSinopse() != null) {
            media.setSinopse(updateMediaDTO.getSinopse());
        }
        if (updateMediaDTO.getCategoria() != null) {
            media.setCategoria(updateMediaDTO.getCategoria());
        }
        if (updateMediaDTO.getMinAge() != null) {
            media.setMinAge(updateMediaDTO.getMinAge());
        }
        if (updateMediaDTO.getAvaliacao() != null) {
            media.setAvaliacao(updateMediaDTO.getAvaliacao());
        }
        if (updateMediaDTO.getEmbed1() != null) {
            media.setEmbed1(updateMediaDTO.getEmbed1());
        }
        if (updateMediaDTO.getEmbed2() != null) {
            media.setEmbed2(updateMediaDTO.getEmbed2());
        }
        if (updateMediaDTO.getTrailer() != null) {
            media.setTrailer(updateMediaDTO.getTrailer());
        }
        if (updateMediaDTO.getImageURL() != null) {
            media.setImageURL(updateMediaDTO.getImageURL());
        }

        Media updatedMedia = mediaRepository.save(media);
        return convertToAdminDTO(updatedMedia);
    }

    /**
     * Deleta uma mídia (e todos os likes/listas relacionados)
     */
    @Transactional
    public boolean deleteMedia( Long mediaId) {

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        // Remove todos os likes relacionados
        likeRepository.deleteByMedia(media);

        // Remove todas as entradas de lista relacionadas
        minhaListaRepository.deleteByMedia(media);

        // Remove a mídia
        try {
            mediaRepository.delete(media);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar mídia: " + e.getMessage());
        }
    }


    /**
     * Retorna estatísticas gerais das mídias
     */
    public MediaStatsDTO getMediaStats(){

        long totalMedias = mediaRepository.count();
        long totalFilmes = mediaRepository.countByIsFilmeTrue();
        long totalSeries = mediaRepository.countByIsFilmeFalse();
        long totalLikes = likeRepository.count();
        long totalUsersWithLists = minhaListaRepository.countDistinctUsers();

        Double averageRating = mediaRepository.getAverageRating();
        if (averageRating == null) averageRating = 0.0;

        String mostLikedMediaTitle = mediaRepository.findMostLikedMediaTitle();
        if (mostLikedMediaTitle == null) mostLikedMediaTitle = "Nenhuma";

        String mostPopularCategory = mediaRepository.findMostPopularCategory();
        if (mostPopularCategory == null) mostPopularCategory = "Nenhuma";

        return new MediaStatsDTO(
                totalMedias,
                totalFilmes,
                totalSeries,
                totalLikes,
                totalUsersWithLists,
                averageRating,
                mostLikedMediaTitle,
                mostPopularCategory
        );
    }

    // Método privado de conversão
    private AdminMediaDTO convertToAdminDTO(Media media) {
        AdminMediaDTO dto = new AdminMediaDTO();
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
        dto.setTotalInLists((long) (media.getMinhaLista() != null ? media.getMinhaLista().size() : 0));
        return dto;
    }
}