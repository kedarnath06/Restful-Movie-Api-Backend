package com.MovieTicketApi.service.implementation;

import com.MovieTicketApi.dto.MovieDto;
import com.MovieTicketApi.dto.MoviePageResponse;
import com.MovieTicketApi.entity.Movie;
import com.MovieTicketApi.exception.FileAlreadyExistsException;
import com.MovieTicketApi.exception.MovieNotFoundException;
import com.MovieTicketApi.repository.MovieRepository;
import com.MovieTicketApi.service.serviceinterface.FileService;
import com.MovieTicketApi.service.serviceinterface.MovieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImp implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;

    public MovieServiceImp(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository=movieRepository;
        this.fileService=fileService;
    }

    @Value("${project.poster:posters/}")
    private String path;

    @Value("${base.url:http//localhost:8080}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        //1. upload the file
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileAlreadyExistsException("File already exists");
        }
        String uploadFileName = fileService.uploadFile(path, file);

        //2.Set the value of field 'poster' as filename
        movieDto.setPoster(uploadFileName);

        //3. map dto to movie obj
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getReleaseYear(),
                movieDto.getMovieCast(),
                movieDto.getPoster()
        );

        //4.save the movie obj-> saved movie obj
        Movie savedMovie = movieRepository.save(movie);

        //5. generate the posterurl
        String posterUrl = baseUrl + "/file/" +uploadFileName;

        //6. map Movie obj to DTO obj and return it
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getReleaseYear(),
                savedMovie.getMovieCast(),
                savedMovie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public MovieDto getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getReleaseYear(),
                movie.getMovieCast(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(response);
        }

        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Long movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        //1. Retrieve the existing movie
        Movie existingMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));

        //2. If a new file is uploaded, update the poster
        if (file != null && !file.isEmpty()) {
            // Delete the old poster if necessary
            String oldPoster = existingMovie.getPoster();
            if (oldPoster != null && !oldPoster.isEmpty()) {
                Files.deleteIfExists(Paths.get(path + File.separator + oldPoster));
            }
            String uploadFileName = fileService.uploadFile(path, file);
            movieDto.setPoster(uploadFileName); // Set new file name in movieDto
        } else {
            // If no new file, keep the existing poster
            movieDto.setPoster(existingMovie.getPoster());
        }

        //3. Update the movie fields
        existingMovie.setTitle(movieDto.getTitle());
        existingMovie.setDirector(movieDto.getDirector());
        existingMovie.setStudio(movieDto.getStudio());
        existingMovie.setReleaseYear(movieDto.getReleaseYear());
        existingMovie.setMovieCast(movieDto.getMovieCast());
        existingMovie.setPoster(movieDto.getPoster());

        //4. Save the updated movie
        Movie updatedMovie = movieRepository.save(existingMovie);

        //5. Generate the poster URL
        String posterUrl = baseUrl + "/file/" + updatedMovie.getPoster();

        //6. Map updated movie to DTO and return
        MovieDto response = new MovieDto(
                updatedMovie.getMovieId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getMovieCast(),
                updatedMovie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public String deleteMovie(Long movieId) throws IOException {
        //1. Retrieve the existing movie
        Movie existingMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));

        //2. Delete the poster file if it exists
        Files.deleteIfExists(Paths.get(path + File.separator + existingMovie.getPoster()));

        //3. Delete the movie from the database
        movieRepository.deleteById(movieId);

        return "Movie deleted with id: " + movieId;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePage = movieRepository.findAll(pageRequest);
        List<Movie> movies = moviePage.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(response);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                moviePage.getTotalElements(), moviePage.getTotalPages(), moviePage.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> moviePage = movieRepository.findAll(pageRequest);
        List<Movie> movies = moviePage.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(response);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                                     moviePage.getTotalElements(),
                                     moviePage.getTotalPages(),
                                     moviePage.isLast());
    }
}
