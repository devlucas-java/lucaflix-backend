package com.lucaflix.service;

import com.lucaflix.dto.admin.CreateAnimeDTO;
import com.lucaflix.dto.admin.UpdateAnimeDTO;
import com.lucaflix.dto.admin.stats.*;
import com.lucaflix.dto.media.AnimeCompleteDTO;
import com.lucaflix.model.Anime;
import com.lucaflix.model.Movie;
import com.lucaflix.model.Serie;
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
public class AdminAnimeService {

    private final AnimeRepository animeRepository;
    private final MovieRepository movieRepository;
    private final SerieRepository serieRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;

    // ==================== GERENCIAMENTO DE ANIMES ====================

    @Transactional
    public AnimeCompleteDTO createAnime(CreateAnimeDTO createDTO) {
        // Verificar se já existe anime com mesmo título e ano
        int year = createDTO.getAnoLancamento().getYear() + 1900;
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
        anime.setCategoria(createDTO.getCategoria());
        anime.setMinAge(createDTO.getMinAge());
        anime.setAvaliacao(createDTO.getAvaliacao());
        anime.setEmbed1(createDTO.getEmbed1());
        anime.setEmbed2(createDTO.getEmbed2());
        anime.setTrailer(createDTO.getTrailer());
        anime.setImageURL1(createDTO.getImageURL1());
        anime.setImageURL2(createDTO.getImageURL2());
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
                int year = updateDTO.getAnoLancamento().getYear() + 1900;
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
        if (updateDTO.getCategoria() != null) {
            anime.setCategoria(updateDTO.getCategoria());
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
        if (updateDTO.getImageURL1() != null) {
            anime.setImageURL1(updateDTO.getImageURL1());
        }
        if (updateDTO.getImageURL2() != null) {
            anime.setImageURL2(updateDTO.getImageURL2());
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

    // ==================== ESTATÍSTICAS COMPLETAS ====================

    public MediaStatsDTO getCompleteStats() {
        MediaStatsDTO stats = new MediaStatsDTO();

        // Contagens básicas
        long totalMovies = movieRepository.count();
        long totalSeries = serieRepository.count();
        long totalAnimes = animeRepository.count();
        long totalMedias = totalMovies + totalSeries + totalAnimes;

        stats.setTotalMedias(totalMedias);
        stats.setTotalFilmes(totalMovies);
        stats.setTotalSeries(totalSeries);
        stats.setTotalAnimes(totalAnimes);

        // Total de likes
        long totalLikes = likeRepository.count();
        stats.setTotalLikes(totalLikes);

        // Usuários com listas
        long totalUsersWithLists = minhaListaRepository.countDistinctUsers();
        stats.setTotalUsersWithLists(totalUsersWithLists);

        // Avaliação média
        Double averageMovieRating = movieRepository.getAverageRating();
        Double averageSerieRating = serieRepository.getAverageRating();
        Double averageAnimeRating = animeRepository.getAverageRating();

        double overallAverage = 0.0;
        int count = 0;

        if (averageMovieRating != null) {
            overallAverage += averageMovieRating;
            count++;
        }
        if (averageSerieRating != null) {
            overallAverage += averageSerieRating;
            count++;
        }
        if (averageAnimeRating != null) {
            overallAverage += averageAnimeRating;
            count++;
        }

        if (count > 0) {
            overallAverage = overallAverage / count;
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

        // Buscar anime mais curtido
        List<Anime> topAnimes = animeRepository.findTop10ByLikes(PageRequest.of(0, 1));
        String topAnimeTitle = null;
        long topAnimeLikes = 0;

        if (!topAnimes.isEmpty()) {
            Anime topAnime = topAnimes.get(0);
            topAnimeTitle = topAnime.getTitle();
            topAnimeLikes = topAnime.getLikes() != null ? topAnime.getLikes().size() : 0;
        }

        // Retornar o mais curtido entre filmes, séries e animes
        long maxLikes = Math.max(topMovieLikes, Math.max(topSerieLikes, topAnimeLikes));

        if (maxLikes == topMovieLikes && topMovieTitle != null) {
            return topMovieTitle + " (" + topMovieLikes + " likes)";
        } else if (maxLikes == topSerieLikes && topSerieTitle != null) {
            return topSerieTitle + " (" + topSerieLikes + " likes)";
        } else if (maxLikes == topAnimeLikes && topAnimeTitle != null) {
            return topAnimeTitle + " (" + topAnimeLikes + " likes)";
        }

        return "N/A";
    }

    private String getMostPopularCategory() {
        List<Object[]> movieCategoryStats = movieRepository.countByCategoria();
        List<Object[]> serieCategoryStats = serieRepository.countByCategoria();
        List<Object[]> animeCategoryStats = animeRepository.countByCategoria();

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

        // Contar categorias de animes
        for (Object[] stat : animeCategoryStats) {
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
        List<Object[]> animeStats = animeRepository.countByCategoria();

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

        // Processar estatísticas de animes
        for (Object[] stat : animeStats) {
            Categoria categoria = (Categoria) stat[0];
            Long count = (Long) stat[1];

            CategoryStatsDTO dto = categoryMap.getOrDefault(categoria, new CategoryStatsDTO());
            dto.setCategoria(categoria.name());
            dto.setTotalAnimes(count);
            categoryMap.put(categoria, dto);
        }

        // Calcular totais
        categoryMap.values().forEach(dto -> {
            dto.setTotalItems(dto.getTotalMovies() + dto.getTotalSeries() + dto.getTotalAnimes());
        });

        return categoryMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalItems(), a.getTotalItems()))
                .collect(java.util.stream.Collectors.toList());
    }

    private List<YearStatsDTO> getYearStats() {
        List<Object[]> movieYearStats = movieRepository.countByYear();
        List<Object[]> serieYearStats = serieRepository.countByYear();
        List<Object[]> animeYearStats = animeRepository.countByYear();

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

        // Processar anos de animes
        for (Object[] stat : animeYearStats) {
            Integer year = (Integer) stat[0];
            Long count = (Long) stat[1];

            YearStatsDTO dto = yearMap.getOrDefault(year, new YearStatsDTO());
            dto.setYear(year);
            dto.setTotalAnimes(count);
            yearMap.put(year, dto);
        }

        // Calcular totais
        yearMap.values().forEach(dto -> {
            dto.setTotalItems(dto.getTotalMovies() + dto.getTotalSeries() + dto.getTotalAnimes());
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

        stats.setHighRatedAnimes(animeRepository.countByAvaliacaoGreaterThanEqual(8.0));
        stats.setMediumRatedAnimes(animeRepository.countByAvaliacaoBetween(6.0, 7.9));
        stats.setLowRatedAnimes(animeRepository.countByAvaliacaoLessThan(6.0));

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
        dto.setCategoria(anime.getCategoria());
        dto.setMinAge(anime.getMinAge());
        dto.setAvaliacao(anime.getAvaliacao());
        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());
        dto.setImageURL1(anime.getImageURL1());
        dto.setImageURL2(anime.getImageURL2());
        dto.setTotalTemporadas(anime.getTotalTemporadas());
        dto.setTotalEpisodios(anime.getTotalEpisodios());
        dto.setTotalLikes((long) (anime.getLikes() != null ? anime.getLikes().size() : 0));
        dto.setUserLiked(false); // Admin não precisa dessa info
        dto.setInUserList(false); // Admin não precisa dessa info
        return dto;
    }
}