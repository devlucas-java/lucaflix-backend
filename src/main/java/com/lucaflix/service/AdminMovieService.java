package com.lucaflix.service;

import com.lucaflix.dto.admin.CreateMovieDTO;
import com.lucaflix.dto.admin.UpdateMovieDTO;
import com.lucaflix.dto.response.movie.MovieCompleteDTO;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminMovieService {

    private final MovieRepository movieRepository;
    private final SerieRepository serieRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;

    // ==================== GERENCIAMENTO DE FILMES ====================
    @Transactional
    public MovieCompleteDTO createMovie(CreateMovieDTO createDTO) {
        // Verificar se já existe filme com mesmo título e ano
        // CORREÇÃO: Remover a soma desnecessária de 1900
        Integer year = createDTO.getAnoLancamento(); // Usar diretamente o valor do DTO

        Optional<Movie> existingMovie = movieRepository.findByTitleAndYear(createDTO.getTitle(), year);
        if (existingMovie.isPresent()) {
            throw new RuntimeException("Já existe um filme com o título '" + createDTO.getTitle() + "' no ano " + year);
        }

        Movie movie = new Movie();
        movie.setTitle(createDTO.getTitle());
        movie.setAnoLancamento(createDTO.getAnoLancamento()); // Usar diretamente
        movie.setDuracaoMinutos(createDTO.getDuracaoMinutos());
        movie.setSinopse(createDTO.getSinopse());
        movie.setCategories(createDTO.getCategories());
        movie.setMinAge(createDTO.getMinAge());
        movie.setAvaliacao(createDTO.getAvaliacao());
        movie.setEmbed1(createDTO.getEmbed1());
        movie.setEmbed2(createDTO.getEmbed2());
        movie.setTrailer(createDTO.getTrailer());
        movie.setPosterURL1(createDTO.getPosterURL1());
        movie.setPosterURL2(createDTO.getPosterURL2());
        movie.setLogoURL1(createDTO.getLogoURL1());
        movie.setLogoURL2(createDTO.getLogoURL2());
        movie.setBackdropURL1(createDTO.getBackdropURL1());
        movie.setBackdropURL2(createDTO.getBackdropURL2());
        movie.setBackdropURL3(createDTO.getBackdropURL3());
        movie.setBackdropURL4(createDTO.getBackdropURL4());
        movie.setDataCadastro(new Date());
        movie.setTmdbId(createDTO.getTmdbId());
        movie.setImdbId(createDTO.getImdbId());
        movie.setPaisOrigen(createDTO.getPaisOrigen());

        Movie savedMovie = movieRepository.save(movie);
        return convertToCompleteDTO(savedMovie);
    }


    @Transactional
    public MovieCompleteDTO updateMovie(Long movieId, UpdateMovieDTO updateDTO) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado"));

        if (updateDTO.getTitle() != null) {
            // Verificar se não há conflito com título/ano se ambos estão sendo alterados
            if (updateDTO.getAnoLancamento() != null) {
                // CORREÇÃO: Usar diretamente o valor do DTO, sem somar 1900
                Integer year = updateDTO.getAnoLancamento();
                Optional<Movie> existingMovie = movieRepository.findByTitleAndYear(updateDTO.getTitle(), year);
                if (existingMovie.isPresent() && !existingMovie.get().getId().equals(movieId)) {
                    throw new RuntimeException("Já existe um filme com o título '" + updateDTO.getTitle() + "' no ano " + year);
                }
            }
            movie.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getAnoLancamento() != null) {
            movie.setAnoLancamento(updateDTO.getAnoLancamento());
        }
        if (updateDTO.getDuracaoMinutos() != null) {
            movie.setDuracaoMinutos(updateDTO.getDuracaoMinutos());
        }
        if (updateDTO.getSinopse() != null) {
            movie.setSinopse(updateDTO.getSinopse());
        }
        if (updateDTO.getCategories() != null) {
            movie.setCategories(updateDTO.getCategories());
        }
        if (updateDTO.getMinAge() != null) {
            movie.setMinAge(updateDTO.getMinAge());
        }
        if (updateDTO.getAvaliacao() != null) {
            movie.setAvaliacao(updateDTO.getAvaliacao());
        }
        if (updateDTO.getEmbed1() != null) {
            movie.setEmbed1(updateDTO.getEmbed1());
        }
        if (updateDTO.getEmbed2() != null) {
            movie.setEmbed2(updateDTO.getEmbed2());
        }
        if (updateDTO.getTrailer() != null) {
            movie.setTrailer(updateDTO.getTrailer());
        }
        if (updateDTO.getLogoURL1() != null) {
            movie.setLogoURL1(updateDTO.getLogoURL1());
        }
        if (updateDTO.getLogoURL2() != null) {
            movie.setLogoURL2(updateDTO.getLogoURL2()); // CORRIGIDO: era setLogoURL1
        }
        if (updateDTO.getBackdropURL1() != null) {
            movie.setBackdropURL1(updateDTO.getBackdropURL1());
        }
        if (updateDTO.getBackdropURL2() != null) {
            movie.setBackdropURL2(updateDTO.getBackdropURL2());
        }
        if (updateDTO.getBackdropURL3() != null) {
            movie.setBackdropURL3(updateDTO.getBackdropURL3());
        }
        if (updateDTO.getBackdropURL4() != null) {
            movie.setBackdropURL4(updateDTO.getBackdropURL4());
        }
        if (updateDTO.getPosterURL1() != null) {
            movie.setPosterURL1(updateDTO.getPosterURL1());
        }
        if (updateDTO.getPosterURL2() != null) {
            movie.setPosterURL2(updateDTO.getPosterURL2());
        }
        if (updateDTO.getImdbId() != null) {
            movie.setImdbId(updateDTO.getImdbId());
        }
        if (updateDTO.getTmdbId() != null) {
            movie.setTmdbId(updateDTO.getTmdbId());
        }
        if (updateDTO.getPaisOrigen() != null) {
            movie.setPaisOrigen(updateDTO.getPaisOrigen());
        }

        Movie updatedMovie = movieRepository.save(movie);
        return convertToCompleteDTO(updatedMovie);
    }

    @Transactional
    public void deleteMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado"));

        // Deletar likes e listas primeiro
        likeRepository.deleteByMovie(movie);
        minhaListaRepository.deleteByMovie(movie);

        movieRepository.delete(movie);
    }

    public MovieCompleteDTO getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado"));
        return convertToCompleteDTO(movie);
    }

    public Page<MovieCompleteDTO> getAllMovies(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Movie> moviesPage = movieRepository.findAll(pageable);
        return moviesPage.map(this::convertToCompleteDTO);
    }

    public Page<MovieCompleteDTO> searchMovies(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> moviesPage = movieRepository.buscarPorFiltros(searchTerm, null, null, pageable);
        return moviesPage.map(this::convertToCompleteDTO);
    }

    // ==================== CONVERSOR ====================

    private MovieCompleteDTO convertToCompleteDTO(Movie movie) {
        MovieCompleteDTO dto = new MovieCompleteDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setAnoLancamento(movie.getAnoLancamento());
        dto.setDuracaoMinutos(movie.getDuracaoMinutos());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setPaisOrigen(movie.getPaisOrigen());
        dto.setSinopse(movie.getSinopse());
        dto.setDataCadastro(movie.getDataCadastro());
        dto.setCategories(movie.getCategories());
        dto.setMinAge(movie.getMinAge());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setEmbed1(movie.getEmbed1());
        dto.setEmbed2(movie.getEmbed2());
        dto.setTrailer(movie.getTrailer());
        dto.setPosterURL1(movie.getPosterURL1());
        dto.setPosterURL2(movie.getPosterURL2());
        dto.setLogoURL1(movie.getLogoURL1());
        dto.setLogoURL2(movie.getLogoURL2());
        dto.setBackdropURL1(movie.getBackdropURL1());
        dto.setBackdropURL2(movie.getBackdropURL2());
        dto.setBackdropURL3(movie.getBackdropURL3());
        dto.setBackdropURL4(movie.getBackdropURL4());
        dto.setTotalLikes((long) (movie.getLikes() != null ? movie.getLikes().size() : 0));
        dto.setUserLiked(false); // Admin não precisa dessa info
        dto.setInUserList(false); // Admin não precisa dessa info
        return dto;
    }
}