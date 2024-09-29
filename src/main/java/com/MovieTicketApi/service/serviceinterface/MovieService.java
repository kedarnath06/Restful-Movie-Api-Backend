package com.MovieTicketApi.service.serviceinterface;

import com.MovieTicketApi.dto.MovieDto;
import com.MovieTicketApi.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovieById(Long movieId);

    List<MovieDto> getAllMovies();

    public MovieDto updateMovie(Long movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    public String deleteMovie(Long movieId) throws IOException;

    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir);
}
