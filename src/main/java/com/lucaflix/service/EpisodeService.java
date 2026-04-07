package com.lucaflix.service;

import com.lucaflix.dto.mapper.EpisodeMapper;
import com.lucaflix.dto.request.serie.CreateEpisodeDTO;
import com.lucaflix.dto.request.serie.UpdateEpisodeDTO;
import com.lucaflix.dto.response.serie.EpisodeDTO;
import com.lucaflix.exception.ResourceNotFoundException;
import com.lucaflix.model.Episode;
import com.lucaflix.repository.EpisodeRepository;
import com.lucaflix.repository.SeasonRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeMapper episodeMapper;

    public EpisodeDTO getEpisode(Long id) {
        Episode episode = episodeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        return episodeMapper.toDTO(episode);
    }

    public EpisodeDTO createEpisode(CreateEpisodeDTO createDTO, Long id) {

        seasonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Season not found"));

        SanitizeUtils.sanitizeStrings(createDTO);
        Episode episode = episodeMapper.toEntity(createDTO);
        episode = episodeRepository.save(episode);

        return episodeMapper.toDTO(episode);
    }

    public EpisodeDTO updateEpisode(UpdateEpisodeDTO updateDTO, Long id) {
        Episode episode = episodeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        SanitizeUtils.sanitizeStrings(updateDTO);

        if (episode.getMinutesDuration() != null) {
            episode.setMinutesDuration(updateDTO.getMinutesDuration());
        }
        if (episode.getNumberEpisode() != null) {
            episode.setNumberEpisode(updateDTO.getNumberEpisode());
        }
        if (episode.getEmbed1() != null) {
            episode.setEmbed1(updateDTO.getEmbed1());
        }
        if (episode.getEmbed2() != null) {
            episode.setEmbed2(updateDTO.getEmbed2());
        }
        if (episode.getSynopsis() != null) {
            episode.setSynopsis(updateDTO.getSynopsis());
        }
        if (episode.getTitle() != null) {
            episode.setTitle(updateDTO.getTitle());
        }
        episode = episodeRepository.save(episode);

        return episodeMapper.toDTO(episode);
    }

    public void deleteEpisode(Long id) {
        episodeRepository.deleteById(id);
    }

}
