package com.eze_dev.torneos.dto.response;

public record PaginationInfoResponseDto(
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious,
        boolean isFirst,
        boolean isLast
) {}
