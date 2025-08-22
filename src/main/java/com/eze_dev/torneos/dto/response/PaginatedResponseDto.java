package com.eze_dev.torneos.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginatedResponseDto<T>(
        List<T> items,
        PaginationInfoResponseDto pagination
) {
    public PaginatedResponseDto(List<T> items, Page<?> page) {
        this(
                items, new PaginationInfoResponseDto(
                        page.getNumber(),
                        page.getTotalPages(),
                        page.getTotalElements(),
                        page.getSize(),
                        page.hasNext(),
                        page.hasPrevious(),
                        page.isFirst(),
                        page.isLast()
                )
        );
    }
}
