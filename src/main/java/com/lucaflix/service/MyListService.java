package com.lucaflix.service;

import com.lucaflix.dto.mapper.AnimeMapper;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.dto.mapper.MovieMapper;
import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.dto.mapper.SerieMapper;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.MyList;
import com.lucaflix.model.User;
import com.lucaflix.repository.MinhaListaRepository;
import com.lucaflix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MyListService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MinhaListaRepository minhaListaRepository;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private SerieMapper serieMapper;
    @Autowired
    private AnimeMapper animeMapper;

    /**
     * Função unificada para obter todos os itens da lista do usuário
     */
    public PaginatedResponseDTO<Object> getMyList(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MyList> myListPage = minhaListaRepository.findByUser(user, pageable);

        List<Object> mediaList = new ArrayList<>();

        for (MyList item : myListPage.getContent()) {
            if (item.getMovie() != null) {
                MovieSimpleDTO movieDTO = movieMapper.convertToSimpleDTO(item.getMovie());
                // Adiciona informação do tipo para identificação no frontend
                movieDTO.setType("MOVIE");
                mediaList.add(movieDTO);
            } else if (item.getSeries() != null) {
                SerieSimpleDTO serieDTO = serieMapper.convertToSimpleDTO(item.getSeries());
                serieDTO.setType("SERIE");
                mediaList.add(serieDTO);
            } else if (item.getAnime() != null) {
                AnimeSimpleDTO animeDTO = animeMapper.convertToSimpleDTO(item.getAnime());
                animeDTO.setType("ANIME");
                mediaList.add(animeDTO);
            }
        }

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
     * Função unificada para obter itens da lista por tipo específico
     */
    public PaginatedResponseDTO<Object> getMyListByType(UUID userId, String type, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MyList> myListPage;

        // Usa diferentes métodos do repository baseado no tipo
        switch (type.toUpperCase()) {
            case "ANIME":
                myListPage = minhaListaRepository.findAnimesByUser(user, pageable);
                break;
            default:
                myListPage = minhaListaRepository.findByUser(user, pageable);
                break;
        }

        List<Object> mediaList = new ArrayList<>();

        for (MyList item : myListPage.getContent()) {
            switch (type.toUpperCase()) {
                case "MOVIE":
                    if (item.getMovie() != null) {
                        MovieSimpleDTO movieDTO = movieMapper.convertToSimpleDTO(item.getMovie());
                        movieDTO.setType("MOVIE");
                        mediaList.add(movieDTO);
                    }
                    break;
                case "SERIE":
                    if (item.getSeries() != null) {
                        SerieSimpleDTO serieDTO = serieMapper.convertToSimpleDTO(item.getSeries());
                        serieDTO.setType("SERIE");
                        mediaList.add(serieDTO);
                    }
                    break;
                case "ANIME":
                    if (item.getAnime() != null) {
                        AnimeSimpleDTO animeDTO = animeMapper.convertToSimpleDTO(item.getAnime());
                        animeDTO.setType("ANIME");
                        mediaList.add(animeDTO);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Tipo inválido: " + type + ". Tipos válidos: MOVIE, SERIE, ANIME");
            }
        }

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

    // Métodos legados mantidos para compatibilidade (podem ser removidos se não utilizados)

    public PaginatedResponseDTO<MovieSimpleDTO> getMyListMovie(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MyList> myListPage = minhaListaRepository.findByUser(user, pageable);

        List<MovieSimpleDTO> mediaList = myListPage.getContent().stream()
                .filter(item -> item.getMovie() != null)
                .map(item -> movieMapper.convertToSimpleDTO(item.getMovie()))
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

    public PaginatedResponseDTO<SerieSimpleDTO> getMyListSerie(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MyList> myListPage = minhaListaRepository.findByUser(user, pageable);

        List<SerieSimpleDTO> seriesList = myListPage.getContent().stream()
                .filter(item -> item.getSeries() != null)
                .map(item -> serieMapper.convertToSimpleDTO(item.getSeries()))
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

    public PaginatedResponseDTO<AnimeSimpleDTO> getMyListAnime(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MyList> myListPage = minhaListaRepository.findAnimesByUser(user, pageable);

        List<AnimeSimpleDTO> animeList = myListPage.getContent().stream()
                .filter(item -> item.getAnime() != null)
                .map(item -> animeMapper.convertToSimpleDTO(item.getAnime()))
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                animeList,
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
}