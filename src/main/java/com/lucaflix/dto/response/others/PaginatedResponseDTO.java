package com.lucaflix.dto.response.others;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponseDTO<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
}