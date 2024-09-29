package com.MovieTicketApi.repository;

import com.MovieTicketApi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie,Long>  {
}
