package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.function.Function;

@RequiredArgsConstructor
public class PageMapper {

    public <T, R> PaginatedResponseDTO<R> toPaginatedDTO(
            Page<T> page,
            Function<T, R> mapperFunction
    ) {

        PaginatedResponseDTO<R> pageDTO = new PaginatedResponseDTO<>();

        pageDTO.setCurrentPage(page.getNumber());
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setSize(page.getSize());
        pageDTO.setFirst(page.isFirst());
        pageDTO.setLast(page.isLast());
        pageDTO.setHasNext(page.hasNext());
        pageDTO.setHasPrevious(page.hasPrevious());

        pageDTO.setContent(
                page.getContent()
                        .stream()
                        .map(mapperFunction)
                        .toList()
        );

        return pageDTO;
    }
}