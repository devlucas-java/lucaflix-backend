package com.lucaflix.service;

import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.mapper.SeriesMapper;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.request.serie.CreateSerieDTO;
import com.lucaflix.dto.request.serie.UpdateSerieDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.Series;
import com.lucaflix.model.User;
import com.lucaflix.repository.SeriesRepository;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import com.lucaflix.service.utils.spec.SeriesSpecification;
import com.lucaflix.service.utils.validate.SeriesValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final UserRepository userRepository;
    private final SeriesValidate seriesValidate;
    private final SeriesMapper seriesMapper;
    private final PageMapper pageMapper;


    public PaginatedResponseDTO<SerieSimpleDTO> filterSeries(FilterDTO filter, int page, int size) {

        if (page < 0){page = 0;}
        if (size <= 0 || size > 100){size = 20;}

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "dateRegistered"));

        SeriesSpecification spec = new SeriesSpecification(filter);
        Page<Series> seriesPage = seriesRepository.findAll(spec, pageable);

        return pageMapper.toPaginatedDTO(seriesPage, a -> seriesMapper.toSimple(a, null));
    }

    public SerieCompleteDTO getSeriesById(UUID id, User userRequest) {
        User user = null;
        if (userRequest != null) {
            user = userRepository.findById(userRequest.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        }
        Series series = seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));

        return seriesMapper.toComplete(series, user);
    }

    public PaginatedResponseDTO<SerieSimpleDTO> getSimilarSeries(UUID id, int page, int size) {
        Series series = seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
        Page<Series> seriesPage = seriesRepository.findSimilarSeries(series.getCategories(), series.getId(), pageable);
        return pageMapper.toPaginatedDTO(seriesPage, s -> seriesMapper.toSimple(s, null));
    }

    public SerieCompleteDTO createSeries(CreateSerieDTO createDTO) {
        SanitizeUtils.sanitizeStrings(createDTO);

        Series series = seriesMapper.toEntity(createDTO);
        seriesRepository.save(series);

        return seriesMapper.toComplete(series, null);
    }

    public SerieCompleteDTO updateSeries(UpdateSerieDTO updateDTO, UUID id) {
        SanitizeUtils.sanitizeStrings(updateDTO);

        Series series = seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));

        seriesValidate.validUpdate(updateDTO, series);
        seriesRepository.save(series);

        return seriesMapper.toComplete(series, null);
    }

    public void deleteSeries(UUID id) {
        Series series = seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));
        seriesRepository.delete(series);
    }
}