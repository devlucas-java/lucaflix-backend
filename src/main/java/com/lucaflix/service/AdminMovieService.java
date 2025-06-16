package com.lucaflix.service;

import com.lucaflix.dto.admin.CreateMovieDTO;
import com.lucaflix.dto.admin.UpdateMovieDTO;
import com.lucaflix.dto.admin.stats.*;
import com.lucaflix.dto.media.MovieCompleteDTO;
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

import java.util.Date;
import java.util.List;
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
        int year = createDTO.getAnoLancamento().getYear() + 1900; // Date.getYear() retorna anos desde 1900
        Optional<Movie> existingMovie = movieRepository.findByTitleAndYear(createDTO.getTitle(), year);
        if (existingMovie.isPresent()) {
            throw new RuntimeException("Já existe um filme com o título '" + createDTO.getTitle() + "' no ano " + year);
        }

        Movie movie = new Movie();
        movie.setTitle(createDTO.getTitle());
        movie.setAnoLancamento(createDTO.getAnoLancamento());
        movie.setDuracaoMinutos(createDTO.getDuracaoMinutos());
        movie.setSinopse(createDTO.getSinopse());
        movie.setCategoria(createDTO.getCategoria());
        movie.setMinAge(createDTO.getMinAge());
        movie.setAvaliacao(createDTO.getAvaliacao());
        movie.setEmbed1(createDTO.getEmbed1());
        movie.setEmbed2(createDTO.getEmbed2());
        movie.setTrailer(createDTO.getTrailer());
        movie.setImageURL1(createDTO.getImageURL1());
        movie.setImageURL2(createDTO.getImageURL2());
        movie.setDataCadastro(new Date());
        movie.setTmdbId(createDTO.getTmdbId());
        movie.setImdbId(createDTO.getImdbId()); // Corrigido: adicionado imdbId
        movie.setPaisOrigen(createDTO.getPaisOrigen()); // Corrigido: era getPaisOrigen(), não getPaisOrigin()

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
                int year = updateDTO.getAnoLancamento().getYear() + 1900;
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
        if (updateDTO.getCategoria() != null) {
            movie.setCategoria(updateDTO.getCategoria());
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
        if (updateDTO.getImageURL1() != null) {
            movie.setImageURL1(updateDTO.getImageURL1());
        }
        if (updateDTO.getImageURL2() != null) {
            movie.setImageURL2(updateDTO.getImageURL2());
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

    // ==================== ESTATÍSTICAS COMPLETAS ====================

    public MediaStatsDTO getCompleteStats() {
        MediaStatsDTO stats = new MediaStatsDTO();

        // Contagens básicas
        long totalMovies = movieRepository.count();
        long totalSeries = serieRepository.count();
        long totalMedias = totalMovies + totalSeries;

        stats.setTotalMedias(totalMedias);
        stats.setTotalFilmes(totalMovies);
        stats.setTotalSeries(totalSeries);

        // Total de likes
        long totalLikes = likeRepository.count();
        stats.setTotalLikes(totalLikes);

        // Usuários com listas
        long totalUsersWithLists = minhaListaRepository.countDistinctUsers();
        stats.setTotalUsersWithLists(totalUsersWithLists);

        // Avaliação média
        Double averageMovieRating = movieRepository.getAverageRating();
        Double averageSerieRating = serieRepository.getAverageRating();

        double overallAverage = 0.0;
        if (averageMovieRating != null && averageSerieRating != null) {
            overallAverage = (averageMovieRating + averageSerieRating) / 2.0;
        } else if (averageMovieRating != null) {
            overallAverage = averageMovieRating;
        } else if (averageSerieRating != null) {
            overallAverage = averageSerieRating;
        }
        stats.setAverageRating(Math.round(overallAverage * 100.0) / 100.0);

        // Mídia mais curtida
        String mostLikedTitle = getMostLikedMediaTitle();
        stats.setMostLikedMediaTitle(mostLikedTitle);

        // Categoria mais popular
        String mostPopularCategory = getMostPopularCategory();
        stats.setMostPopularCategory(mostPopularCategory);

        return stats;
    }

    public DetailedStatsDTO getDetailedStats() {
        DetailedStatsDTO stats = new DetailedStatsDTO();

        // Estatísticas básicas
        MediaStatsDTO basicStats = getCompleteStats();
        stats.setBasicStats(basicStats);

        // Estatísticas por categoria
        stats.setCategoryStats(getCategoryStats());

        // Estatísticas por ano
        stats.setYearStats(getYearStats());

        // Estatísticas de usuários
        stats.setUserStats(getUserStats());

        // Estatísticas de qualidade (por avaliação)
        stats.setQualityStats(getQualityStats());

        return stats;
    }

    // ==================== MÉTODOS AUXILIARES PARA ESTATÍSTICAS ====================

    private String getMostLikedMediaTitle() {
        // Buscar filme mais curtido
        List<Movie> topMovies = movieRepository.findTop10ByLikes(PageRequest.of(0, 1));
        String topMovieTitle = null;
        long topMovieLikes = 0;

        if (!topMovies.isEmpty()) {
            Movie topMovie = topMovies.get(0);
            topMovieTitle = topMovie.getTitle();
            topMovieLikes = topMovie.getLikes() != null ? topMovie.getLikes().size() : 0;
        }

        // Buscar série mais curtida
        List<Serie> topSeries = serieRepository.findTop10ByLikes(PageRequest.of(0, 1));
        String topSerieTitle = null;
        long topSerieLikes = 0;

        if (!topSeries.isEmpty()) {
            Serie topSerie = topSeries.get(0);
            topSerieTitle = topSerie.getTitle();
            topSerieLikes = topSerie.getLikes() != null ? topSerie.getLikes().size() : 0;
        }

        // Retornar o mais curtido entre filmes e séries
        if (topMovieLikes >= topSerieLikes) {
            return topMovieTitle != null ? topMovieTitle + " (" + topMovieLikes + " likes)" : "N/A";
        } else {
            return topSerieTitle != null ? topSerieTitle + " (" + topSerieLikes + " likes)" : "N/A";
        }
    }

    private String getMostPopularCategory() {
        List<Object[]> movieCategoryStats = movieRepository.countByCategoria();
        List<Object[]> serieCategoryStats = serieRepository.countByCategoria();

        java.util.Map<Categoria, Long> categoryCount = new java.util.HashMap<>();

        // Contar categorias de filmes
        for (Object[] stat : movieCategoryStats) {
            Categoria categoria = (Categoria) stat[0];
            Long count = (Long) stat[1];
            categoryCount.put(categoria, categoryCount.getOrDefault(categoria, 0L) + count);
        }

        // Contar categorias de séries
        for (Object[] stat : serieCategoryStats) {
            Categoria categoria = (Categoria) stat[0];
            Long count = (Long) stat[1];
            categoryCount.put(categoria, categoryCount.getOrDefault(categoria, 0L) + count);
        }

        // Encontrar a categoria mais popular
        return categoryCount.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().name() + " (" + entry.getValue() + " itens)")
                .orElse("N/A");
    }

    private List<CategoryStatsDTO> getCategoryStats() {
        List<Object[]> movieStats = movieRepository.countByCategoria();
        List<Object[]> serieStats = serieRepository.countByCategoria();

        java.util.Map<Categoria, CategoryStatsDTO> categoryMap = new java.util.HashMap<>();

        // Processar estatísticas de filmes
        for (Object[] stat : movieStats) {
            Categoria categoria = (Categoria) stat[0];
            Long count = (Long) stat[1];

            CategoryStatsDTO dto = categoryMap.getOrDefault(categoria, new CategoryStatsDTO());
            dto.setCategoria(categoria.name());
            dto.setTotalMovies(count);
            categoryMap.put(categoria, dto);
        }

        // Processar estatísticas de séries
        for (Object[] stat : serieStats) {
            Categoria categoria = (Categoria) stat[0];
            Long count = (Long) stat[1];

            CategoryStatsDTO dto = categoryMap.getOrDefault(categoria, new CategoryStatsDTO());
            dto.setCategoria(categoria.name());
            dto.setTotalSeries(count);
            categoryMap.put(categoria, dto);
        }

        // Calcular totais
        categoryMap.values().forEach(dto -> {
            dto.setTotalItems(dto.getTotalMovies() + dto.getTotalSeries());
        });

        return categoryMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalItems(), a.getTotalItems()))
                .collect(java.util.stream.Collectors.toList());
    }

    private List<YearStatsDTO> getYearStats() {
        List<Object[]> movieYearStats = movieRepository.countByYear();
        List<Object[]> serieYearStats = serieRepository.countByYear();

        java.util.Map<Integer, YearStatsDTO> yearMap = new java.util.HashMap<>();

        // Processar anos de filmes
        for (Object[] stat : movieYearStats) {
            Integer year = (Integer) stat[0];
            Long count = (Long) stat[1];

            YearStatsDTO dto = yearMap.getOrDefault(year, new YearStatsDTO());
            dto.setYear(year);
            dto.setTotalMovies(count);
            yearMap.put(year, dto);
        }

        // Processar anos de séries
        for (Object[] stat : serieYearStats) {
            Integer year = (Integer) stat[0];
            Long count = (Long) stat[1];

            YearStatsDTO dto = yearMap.getOrDefault(year, new YearStatsDTO());
            dto.setYear(year);
            dto.setTotalSeries(count);
            yearMap.put(year, dto);
        }

        // Calcular totais
        yearMap.values().forEach(dto -> {
            dto.setTotalItems(dto.getTotalMovies() + dto.getTotalSeries());
        });

        return yearMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getYear(), a.getYear()))
                .limit(10) // Top 10 anos mais recentes
                .collect(java.util.stream.Collectors.toList());
    }

    private UserStatsDTO getUserStats() {
        UserStatsDTO stats = new UserStatsDTO();

        stats.setTotalUsers(userRepository.count());
        stats.setUsersWithLists(minhaListaRepository.countDistinctUsers());
        stats.setAverageLikesPerUser(calculateAverageLikesPerUser());
        stats.setAverageListItemsPerUser(calculateAverageListItemsPerUser());

        return stats;
    }



    private QualityStatsDTO getQualityStats() {
        QualityStatsDTO stats = new QualityStatsDTO();

        stats.setHighRatedMovies(movieRepository.countByAvaliacaoGreaterThanEqual(8.0));
        stats.setMediumRatedMovies(movieRepository.countByAvaliacaoBetween(6.0, 7.9));
        stats.setLowRatedMovies(movieRepository.countByAvaliacaoLessThan(6.0));

        return stats;
    }

    private double calculateAverageLikesPerUser() {
        long totalUsers = userRepository.count();
        long totalLikes = likeRepository.count();
        return totalUsers > 0 ? (double) totalLikes / totalUsers : 0.0;
    }

    private double calculateAverageListItemsPerUser() {
        long totalUsers = userRepository.count();
        long totalListItems = minhaListaRepository.count();
        return totalUsers > 0 ? (double) totalListItems / totalUsers : 0.0;
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
        dto.setCategoria(movie.getCategoria());
        dto.setMinAge(movie.getMinAge());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setEmbed1(movie.getEmbed1());
        dto.setEmbed2(movie.getEmbed2());
        dto.setTrailer(movie.getTrailer());
        dto.setImageURL1(movie.getImageURL1());
        dto.setImageURL2(movie.getImageURL2());
        dto.setTotalLikes((long) (movie.getLikes() != null ? movie.getLikes().size() : 0));
        dto.setUserLiked(false); // Admin não precisa dessa info
        dto.setInUserList(false); // Admin não precisa dessa info
        return dto;
    }
}