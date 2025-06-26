package com.lucaflix.service;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.dto.media.serie.SerieCompleteDTO;
import com.lucaflix.dto.media.serie.SerieMapper;
import com.lucaflix.dto.media.serie.SerieSimpleDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SerieService {

    private final SerieRepository serieRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;
    private final SerieMapper serieMapper;

    // Método para debug - verificar se existem séries
    public long getTotalSeriesCount() {
        long count = serieRepository.countAllSeries();
        log.info("Total de séries no banco: {}", count);
        return count;
    }

    // Todas as séries - CORRIGIDA com debug
    public PaginatedResponseDTO<SerieSimpleDTO> getAllSeries(int page, int size) {
        log.info("Buscando todas as séries - página: {}, tamanho: {}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
            Page<Serie> seriesPage = serieRepository.findAll(pageable);

            log.info("Séries encontradas: {}", seriesPage.getTotalElements());

            if (seriesPage.isEmpty()) {
                log.warn("Nenhuma série encontrada na página {}. Total de séries: {}", page, getTotalSeriesCount());
            }

            return serieMapper.createPaginatedResponse(seriesPage);
        } catch (Exception e) {
            log.error("Erro ao buscar todas as séries: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar séries");
        }
    }

    // Top 10 mais curtidas - CORRIGIDA para respeitar o size
    public List<SerieSimpleDTO> getTop10MostLiked() {
        log.info("Buscando top 10 séries mais curtidas");

        try {
            // Usar exatamente 10 como hardcoded para top 10
            List<Serie> topSeries = serieRepository.findTop10ByLikes(PageRequest.of(0, 10));
            log.info("Top 10 séries encontradas: {}", topSeries.size());

            return topSeries.stream()
                    .map(serieMapper::convertToSimpleDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao buscar top 10 séries: ", e);
            // Fallback para séries normais se der erro
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> fallbackSeries = serieRepository.findAll(pageable);
            return fallbackSeries.getContent().stream()
                    .map(serieMapper::convertToSimpleDTO)
                    .collect(Collectors.toList());
        }
    }

    // Obter série por ID - CORRIGIDA para resolver MultipleBagFetchException
    public SerieCompleteDTO getSerieById(Long serieId, UUID userId) {
        log.info("Buscando série por ID: {}", serieId);

        // Primeiro, buscar a série com temporadas
        Serie serie = serieRepository.findByIdWithTemporadas(serieId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Série não encontrada"
                ));

        // Depois, buscar as temporadas com episódios em consulta separada
        List<Temporada> temporadasComEpisodios = serieRepository.findTemporadasWithEpisodiosBySerieId(serieId);

        // Associar as temporadas com episódios à série
        if (temporadasComEpisodios != null && !temporadasComEpisodios.isEmpty()) {
            serie.setTemporadas(temporadasComEpisodios);
        }

        log.info("Série encontrada: {} com {} temporadas", serie.getTitle(),
                serie.getTemporadas() != null ? serie.getTemporadas().size() : 0);

        return serieMapper.convertToCompleteDTO(serie, userId);
    }

    // Toggle Like - MANTIDA
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

    // Toggle Minha Lista - MANTIDA
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

    // Séries por categoria - CORRIGIDA
    public PaginatedResponseDTO<SerieSimpleDTO> getSeriesByCategory(Categoria categoria, int page, int size) {
        log.info("Buscando séries por categoria: {} - página: {}, tamanho: {}", categoria, page, size);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
            Page<Serie> seriesPage = serieRepository.findByCategoria(categoria, pageable);

            log.info("Séries encontradas para categoria {}: {}", categoria, seriesPage.getTotalElements());

            if (seriesPage.isEmpty()) {
                log.warn("Nenhuma série encontrada para categoria: {}", categoria);
            }

            return serieMapper.createPaginatedResponse(seriesPage);
        } catch (Exception e) {
            log.error("Erro ao buscar séries por categoria {}: ", categoria, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao buscar séries por categoria");
        }
    }

    // Séries populares - CORRIGIDA para respeitar size
    public PaginatedResponseDTO<SerieSimpleDTO> getPopularSeries(int page, int size) {
        log.info("Buscando séries populares - página: {}, tamanho: {}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size); // Respeitando o size do controller
            Page<Serie> seriesPage = serieRepository.findPopularSeries(pageable);

            log.info("Séries populares encontradas: {}", seriesPage.getTotalElements());

            if (seriesPage.isEmpty()) {
                // Fallback para todas as séries ordenadas por avaliação
                log.warn("Nenhuma série popular encontrada, usando fallback");
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
                seriesPage = serieRepository.findAll(pageable);
            }

            return serieMapper.createPaginatedResponse(seriesPage);
        } catch (Exception e) {
            log.error("Erro ao buscar séries populares: ", e);
            // Fallback respeitando o size
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> seriesPage = serieRepository.findAll(pageable);
            return serieMapper.createPaginatedResponse(seriesPage);
        }
    }

    // Séries com avaliação alta - CORRIGIDA para respeitar size
    public PaginatedResponseDTO<SerieSimpleDTO> getHighRatedSeries(int page, int size) {
        log.info("Buscando séries com avaliação alta - página: {}, tamanho: {}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> seriesPage = serieRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);

            log.info("Séries com avaliação alta encontradas: {}", seriesPage.getTotalElements());

            if (seriesPage.isEmpty()) {
                // Fallback para avaliação mais baixa respeitando o size
                log.warn("Nenhuma série com avaliação >= 7.0, tentando >= 5.0");
                seriesPage = serieRepository.findByAvaliacaoGreaterThanEqual(5.0, pageable);

                if (seriesPage.isEmpty()) {
                    // Fallback final - todas as séries respeitando o size
                    log.warn("Nenhuma série com avaliação >= 5.0, retornando todas");
                    seriesPage = serieRepository.findAll(pageable);
                }
            }

            return serieMapper.createPaginatedResponse(seriesPage);
        } catch (Exception e) {
            log.error("Erro ao buscar séries com avaliação alta: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao buscar séries com avaliação alta");
        }
    }

    // Séries recentes - CORRIGIDA para respeitar size
    public PaginatedResponseDTO<SerieSimpleDTO> getRecentSeries(int page, int size) {
        log.info("Buscando séries recentes - página: {}, tamanho: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "anoLancamento"));
        Page<Serie> seriesPage = serieRepository.findAll(pageable);

        log.info("Séries recentes encontradas: {}", seriesPage.getTotalElements());

        return serieMapper.createPaginatedResponse(seriesPage);
    }

    // Séries por ano - CORRIGIDA
    public PaginatedResponseDTO<SerieSimpleDTO> getSeriesByYear(Integer year, int page, int size) {
        log.info("Buscando séries do ano: {} - página: {}, tamanho: {}", year, page, size);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> seriesPage = serieRepository.findByYear(year, pageable);

            log.info("Séries do ano {} encontradas: {}", year, seriesPage.getTotalElements());

            if (seriesPage.isEmpty()) {
                log.warn("Nenhuma série encontrada para o ano: {}", year);
            }

            return serieMapper.createPaginatedResponse(seriesPage);
        } catch (Exception e) {
            log.error("Erro ao buscar séries do ano {}: ", year, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao buscar séries do ano " + year);
        }
    }

    // Séries similares - CORRIGIDA para respeitar size
    public PaginatedResponseDTO<SerieSimpleDTO> getSimilarSeries(Long serieId, int page, int size) {
        log.info("Buscando séries similares à série ID: {} - página: {}, tamanho: {}", serieId, page, size);

        try {
            Serie serie = serieRepository.findById(serieId)
                    .orElseThrow(() -> new RuntimeException("Série não encontrada"));

            if (serie.getCategoria() == null || serie.getCategoria().isEmpty()) {
                log.warn("Série {} não possui categorias, retornando séries aleatórias", serieId);
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
                Page<Serie> seriesPage = serieRepository.findAll(pageable);
                return serieMapper.createPaginatedResponse(seriesPage);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> seriesPage = serieRepository.findSimilarSeries(serie.getCategoria(), serie.getId(), pageable);

            log.info("Séries similares encontradas: {}", seriesPage.getTotalElements());

            if (seriesPage.isEmpty()) {
                log.warn("Nenhuma série similar encontrada para a série {}", serieId);
            }

            return serieMapper.createPaginatedResponse(seriesPage);
        } catch (Exception e) {
            log.error("Erro ao buscar séries similares: ", e);
            // Fallback respeitando o size
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> seriesPage = serieRepository.findAll(pageable);
            return serieMapper.createPaginatedResponse(seriesPage);
        }
    }

    // Recomendações - CORRIGIDA para respeitar size
    public PaginatedResponseDTO<SerieSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        log.info("Buscando recomendações para usuário: {} - página: {}, tamanho: {}", userId, page, size);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> seriesPage = serieRepository.findRecommendations(userId, pageable);

            log.info("Recomendações encontradas: {}", seriesPage.getTotalElements());

            if (seriesPage.isEmpty()) {
                log.warn("Nenhuma recomendação encontrada, usando séries populares como fallback");
                seriesPage = serieRepository.findAll(pageable);
            }

            return serieMapper.createPaginatedResponse(seriesPage);
        } catch (Exception e) {
            log.error("Erro ao buscar recomendações: ", e);
            // Fallback respeitando o size
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
            Page<Serie> seriesPage = serieRepository.findAll(pageable);
            return serieMapper.createPaginatedResponse(seriesPage);
        }
    }
}