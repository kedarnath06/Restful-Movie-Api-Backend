package com.MovieTicketApi.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDtos, Integer pageNumber, Integer pageSize, Long totalElement,Integer totalPage, Boolean isLast) {
}
