package com.lucaflix.service;

import com.lucaflix.dto.request.anime.CreateAnimeDTO;
import com.lucaflix.dto.request.anime.UpdateAnimeDTO;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.model.Anime;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAnimeService {

    private final AnimeRepository animeRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;


    // ==================== GERENCIAMENTO DE ANIMES ====================

    @Transactional
    public AnimeCompleteDTO createAnime(CreateAnimeDTO createDTO) {
        // Verificar se já existe anime com mesmo título e ano
        int year = createDTO.getAnoLancamento() + 1900;
        Optional<Anime> existingAnime = animeRepository.findByTitleAndYear(createDTO.getTitle(), year);
        if (existingAnime.isPresent()) {
            throw new RuntimeException("Já existe um anime com o título '" + createDTO.getTitle() + "' no ano " + year);
        }

        Anime anime = new Anime();
        anime.setTitle(createDTO.getTitle());
        anime.setAnoLancamento(createDTO.getAnoLancamento());
        anime.setTmdbId(createDTO.getTmdbId());
        anime.setImdbId(createDTO.getImdbId());
        anime.setPaisOrigen(createDTO.getPaisOrigen());
        anime.setSinopse(createDTO.getSinopse());
        anime.setCategories(createDTO.getCategories());
        anime.setMinAge(createDTO.getMinAge());
        anime.setAvaliacao(createDTO.getAvaliacao());
        anime.setEmbed1(createDTO.getEmbed1());
        anime.setEmbed2(createDTO.getEmbed2());
        anime.setTrailer(createDTO.getTrailer());
        anime.setLogoURL1(createDTO.getLogoURL1());
        anime.setLogoURL2(createDTO.getLogoURL2());
        anime.setBackdropURL1(createDTO.getBackdropURL1());
        anime.setBackdropURL2(createDTO.getBackdropURL2());
        anime.setBackdropURL3(createDTO.getBackdropURL3());
        anime.setBackdropURL4(createDTO.getBackdropURL4());
        anime.setPosterURL1(createDTO.getPosterURL1());
        anime.setPosterURL2(createDTO.getPosterURL2());
        anime.setTotalTemporadas(createDTO.getTotalTemporadas());
        anime.setTotalEpisodios(createDTO.getTotalEpisodios());
        anime.setDataCadastro(new Date());

        Anime savedAnime = animeRepository.save(anime);
        return convertToCompleteDTO(savedAnime);
    }

    @Transactional
    public AnimeCompleteDTO updateAnime(Long animeId, UpdateAnimeDTO updateDTO) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime não encontrado"));

        if (updateDTO.getTitle() != null) {
            // Verificar se não há conflito com título/ano se ambos estão sendo alterados
            if (updateDTO.getAnoLancamento() != null) {
                int year = updateDTO.getAnoLancamento() + 1900;
                Optional<Anime> existingAnime = animeRepository.findByTitleAndYear(updateDTO.getTitle(), year);
                if (existingAnime.isPresent() && !existingAnime.get().getId().equals(animeId)) {
                    throw new RuntimeException("Já existe um anime com o título '" + updateDTO.getTitle() + "' no ano " + year);
                }
            }
            anime.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getAnoLancamento() != null) {
            anime.setAnoLancamento(updateDTO.getAnoLancamento());
        }
        if (updateDTO.getTmdbId() != null) {
            anime.setTmdbId(updateDTO.getTmdbId());
        }
        if (updateDTO.getImdbId() != null) {
            anime.setImdbId(updateDTO.getImdbId());
        }
        if (updateDTO.getPaisOrigen() != null) {
            anime.setPaisOrigen(updateDTO.getPaisOrigen());
        }
        if (updateDTO.getSinopse() != null) {
            anime.setSinopse(updateDTO.getSinopse());
        }
        if (updateDTO.getCategories() != null) {
            anime.setCategories(updateDTO.getCategories());
        }
        if (updateDTO.getMinAge() != null) {
            anime.setMinAge(updateDTO.getMinAge());
        }
        if (updateDTO.getAvaliacao() != null) {
            anime.setAvaliacao(updateDTO.getAvaliacao());
        }
        if (updateDTO.getEmbed1() != null) {
            anime.setEmbed1(updateDTO.getEmbed1());
        }
        if (updateDTO.getEmbed2() != null) {
            anime.setEmbed2(updateDTO.getEmbed2());
        }
        if (updateDTO.getTrailer() != null) {
            anime.setTrailer(updateDTO.getTrailer());
        }
        if (updateDTO.getLogoURL1() != null) {
            anime.setLogoURL1(updateDTO.getLogoURL1());
        }
        if (updateDTO.getLogoURL2() != null) {
            anime.setLogoURL2(updateDTO.getLogoURL2());
        }
        if (updateDTO.getBackdropURL1() != null) {
            anime.setBackdropURL1(updateDTO.getBackdropURL1());
        }
        if (updateDTO.getBackdropURL2() != null) {
            anime.setBackdropURL2(updateDTO.getBackdropURL2());
        }
        if (updateDTO.getBackdropURL3() != null) {
            anime.setBackdropURL3(updateDTO.getBackdropURL3());
        }
        if (updateDTO.getBackdropURL4() != null) {
            anime.setBackdropURL4(updateDTO.getBackdropURL4());
        }
        if (updateDTO.getPosterURL1() != null) {
            anime.setPosterURL1(updateDTO.getPosterURL1());
        }
        if (updateDTO.getPosterURL2() != null) {
            anime.setPosterURL2(updateDTO.getPosterURL2());
        }
        if (updateDTO.getTotalTemporadas() != null) {
            anime.setTotalTemporadas(updateDTO.getTotalTemporadas());
        }
        if (updateDTO.getTotalEpisodios() != null) {
            anime.setTotalEpisodios(updateDTO.getTotalEpisodios());
        }

        Anime updatedAnime = animeRepository.save(anime);
        return convertToCompleteDTO(updatedAnime);
    }

    @Transactional
    public void deleteAnime(Long animeId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime não encontrado"));

        // Deletar likes e listas primeiro
        likeRepository.deleteByAnime(anime);
        minhaListaRepository.deleteByAnime(anime);

        animeRepository.delete(anime);
    }

    public AnimeCompleteDTO getAnimeById(Long animeId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime não encontrado"));
        return convertToCompleteDTO(anime);
    }

    public Page<AnimeCompleteDTO> getAllAnimes(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Anime> animesPage = animeRepository.findAll(pageable);
        return animesPage.map(this::convertToCompleteDTO);
    }

    public Page<AnimeCompleteDTO> searchAnimes(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animesPage = animeRepository.buscarPorFiltros(searchTerm, null, null, pageable);
        return animesPage.map(this::convertToCompleteDTO);
    }

    // ==================== CONVERSOR ====================

    private AnimeCompleteDTO convertToCompleteDTO(Anime anime) {
        AnimeCompleteDTO dto = new AnimeCompleteDTO();
        dto.setId(anime.getId());
        dto.setTitle(anime.getTitle());
        dto.setAnoLancamento(anime.getAnoLancamento());
        dto.setTmdbId(anime.getTmdbId());
        dto.setImdbId(anime.getImdbId());
        dto.setPaisOrigen(anime.getPaisOrigen());
        dto.setSinopse(anime.getSinopse());
        dto.setDataCadastro(anime.getDataCadastro());
        dto.setCategories(anime.getCategories());
        dto.setMinAge(anime.getMinAge());
        dto.setAvaliacao(anime.getAvaliacao());
        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());
        dto.setLogoURL1(anime.getLogoURL1());
        dto.setLogoURL2(anime.getLogoURL2());
        dto.setBackdropURL1(anime.getBackdropURL1());
        dto.setBackdropURL2(anime.getBackdropURL2());
        dto.setBackdropURL3(anime.getBackdropURL3());
        dto.setBackdropURL4(anime.getBackdropURL4());
        dto.setPosterURL1(anime.getPosterURL1());
        dto.setPosterURL2(anime.getPosterURL2());
        dto.setTotalTemporadas(anime.getTotalTemporadas());
        dto.setTotalEpisodios(anime.getTotalEpisodios());
        dto.setTotalLikes((long) (anime.getLikes() != null ? anime.getLikes().size() : 0));
        dto.setUserLiked(false); // Admin não precisa dessa info
        dto.setInUserList(false); // Admin não precisa dessa info
        return dto;
    }
}